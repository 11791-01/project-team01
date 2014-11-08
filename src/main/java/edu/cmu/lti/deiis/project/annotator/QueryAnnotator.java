/**
 * 
 */
package edu.cmu.lti.deiis.project.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.AtomicQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import util.Utils;

/**
 * @author Zexi Mao
 *
 */
public class QueryAnnotator extends JCasAnnotator_ImplBase {

  /** 
   * Create queries for questions.
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Question.type).iterator();
    
    if (iter.isValid() && iter.hasNext()) {
      Question question = (Question) iter.next();
      
      AtomicQueryConcept atomicQuery = new AtomicQueryConcept(aJCas);
      atomicQuery.setText(question.getText().replace("?", ""));
      System.out.println(atomicQuery.getText());
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