package edu.cmu.lti.deiis.project.annotator;

import java.util.ArrayList;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import util.Utils;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import abner.Tagger;

/**
 * An annotator based on ABNER.
 * 
 * @author Zexi Mao <zexim@cs.cmu.edu>
 * 
 */
public class AbnerAnnotator extends JCasAnnotator_ImplBase {

  private Tagger mTagger;

  /**
   * Initialize the tagger using NLPBA model.
   * 
   * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(UimaContext)
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    // Load the BioCreative model
    mTagger = new Tagger(Tagger.NLPBA);
  }

  /**
   * Reads a sentence and annotate it with ABNER.
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> quesIter = aJCas.getAnnotationIndex(Question.type).iterator();
    FSIterator<TOP> queryIter = aJCas.getJFSIndexRepository().getAllIndexedFS(
            ComplexQueryConcept.type);
    
    try {
      // Get the question first.
      Question question = (Question) quesIter.next();
      ComplexQueryConcept query = (ComplexQueryConcept) queryIter.next();
      String queString = question.getText();
      String[][] entities = mTagger.getEntities(queString);
      
      ArrayList<String> tags = new ArrayList<String>();
      String tag = "";
      if (entities[1].length > 0) {
        if (entities[1][0].equals("PROTEIN")) {
          tag = entities[1][0];
        } else {
          tag = "GENE";
        }
      }
      System.out.println(tag);
      tags.add(tag);
      query.setNamedEntityTypes(Utils.createStringList(aJCas, tags));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
