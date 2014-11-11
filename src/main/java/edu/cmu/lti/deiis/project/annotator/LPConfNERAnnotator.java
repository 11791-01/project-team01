/**
 * 
 */
package edu.cmu.lti.deiis.project.annotator;

import java.io.File;
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

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.ConfidenceChunker;
import com.aliasi.util.AbstractExternalizable;

import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.AtomicQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import util.Utils;

/**
 * @author Zexi Mao
 *
 */
public class LPConfNERAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Name of configuration parameter that must be set to the path of the model file.
   */
  public static final String PARAM_MODEL_FILE = "ModelFile";
  
  /**
   * Name of configuration parameter that must be set to the max chunk number.
   */
  public static final String PARAM_MAX_N_BEST_CHUNKS = "MaxNBestChunks";
  
  /**
   * Name of configuration parameter that must be set to the threshold.
   */
  public static final String PARAM_THRESHOLD = "Threshold";
  
  private ConfidenceChunker mChunker;
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
    
    mMAX_N_BEST_CHUNKS = (Integer) aContext.getConfigParameterValue(PARAM_MAX_N_BEST_CHUNKS);
    mThreashold = (Float) aContext.getConfigParameterValue(PARAM_THRESHOLD);
    
    try {
      File modelFile = new File(((String) aContext.getConfigParameterValue(PARAM_MODEL_FILE)).trim());
      mChunker = (ConfidenceChunker) AbstractExternalizable.readObject(modelFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  /** 
   * Create queries for questions.
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Question.type).iterator();
    
    if (iter.isValid() && iter.hasNext()) {
      Question question = (Question) iter.next();
      
      String queString = question.getText();
      char[] cs = queString.toCharArray();
      Iterator<Chunk> it = mChunker.nBestChunks(cs, 0, cs.length, mMAX_N_BEST_CHUNKS);
      String queryString = "";
      
      /*
      while (it.hasNext()) {
        Chunk chunk = (Chunk) it.next();
        queryString += queString.substring(chunk.start(), chunk.end());
        queryString += " ";
        System.out.println(queString.substring(chunk.start(), chunk.end()));
        System.out.println(chunk.score());
      }
      
      System.out.println(queString);
      System.out.println(queryString);
      */
      
      AtomicQueryConcept atomicQuery = new AtomicQueryConcept(aJCas);
      //atomicQuery.setText(question.getText().replace("?", ""));
      atomicQuery.setText(queryString);
      atomicQuery.addToIndexes();
      List<AtomicQueryConcept> terms = new ArrayList<AtomicQueryConcept>();
      terms.add(atomicQuery);
      
      // Create the query for the following Annotators.
      ComplexQueryConcept query = new ComplexQueryConcept(aJCas);
      query.setOperatorArgs(Utils.fromCollectionToFSList(aJCas, terms));
      query.addToIndexes();
    }
  }

}