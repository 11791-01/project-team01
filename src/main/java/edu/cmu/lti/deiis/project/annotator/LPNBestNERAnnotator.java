/**
 * 
 */
package edu.cmu.lti.deiis.project.annotator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.chunk.Chunking;
import com.aliasi.chunk.NBestChunker;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.ScoredObject;

import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.AtomicQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import util.Utils;

/**
 * An annotator that generates query terms for each question using LingPipe Statistical NER.
 * 
 * @author Zexi Mao <zexim@cs.cmu.edu>
 *
 */
public class LPNBestNERAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Name of configuration parameter that must be set to the path of the model file.
   */
  public static final String PARAM_MODEL_FILE = "ModelFile";

  // LingPipe chunker
  private NBestChunker mChunker;

  private String mModelPath;

  private Integer mMAX_N_BEST_CHUNKS;

  private Float mThreashold;

  /**
   * Initialize the annotator, create the chunker in LinePipe.
   * 
   * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(UimaContext)
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    // Load the LingPipe pre-trained model
    try {
      mChunker = (NBestChunker) AbstractExternalizable.readResourceObject(
              LPNBestNERAnnotator.class,
              (String) aContext.getConfigParameterValue(PARAM_MODEL_FILE));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create queries for each question.
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Question.type).iterator();

    if (iter.isValid() && iter.hasNext()) {
      // Get the questions
      Question question = (Question) iter.next();

      String queString = question.getText();
      char[] cs = queString.toCharArray();
      Iterator<ScoredObject<Chunking>> it = mChunker.nBest(cs, 0, cs.length, 3);
      String queryString = "";

      while (it.hasNext()) {
        ScoredObject<Chunking> so = it.next();
        System.out.println(so.score() + " " + so.getObject());
      }
      System.out.println(queString);
      //System.out.println(queryString);

      // Create an atomic query first
      AtomicQueryConcept atomicQuery = new AtomicQueryConcept(aJCas);
      atomicQuery.setText(queryString);
      atomicQuery.addToIndexes();
      List<AtomicQueryConcept> terms = new ArrayList<AtomicQueryConcept>();
      terms.add(atomicQuery);

      // Create the complex query for the following Annotators.
      ComplexQueryConcept query = new ComplexQueryConcept(aJCas);
      query.setOperatorArgs(Utils.fromCollectionToFSList(aJCas, terms));
      query.addToIndexes();
    }
  }

}