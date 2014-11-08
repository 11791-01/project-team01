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
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex().iterator();
    Question question = null;
    if (iter.isValid()) {
      iter.moveToNext();
       question = (Question) iter.get();
    }
    
    AtomicQueryConcept atomicQuery = new AtomicQueryConcept(aJCas);
    atomicQuery.setText(question.getText());
    List<AtomicQueryConcept> terms = new ArrayList<AtomicQueryConcept>();
    terms.add(atomicQuery);
    
    // Create the query for the following Annotators.
    ComplexQueryConcept query = new ComplexQueryConcept(aJCas);
    query.setOperatorArgs(Utils.fromCollectionToFSList(aJCas, terms));
    query.addToIndexes(aJCas);
  }

}