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
import org.apache.uima.jcas.cas.TOP;

import util.Utils;
import edu.cmu.lti.oaqa.type.retrieval.AtomicQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.QueryOperator;

/**
 * An annotator used to combine the atomic queries into a complex query.
 * 
 * @author Zexi Mao <zexim@cs.cmu.edu>
 *
 */
public class QueryCombiner extends JCasAnnotator_ImplBase {

  /**
   * Reads all atomic queries and put them into one complext query.
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<TOP> it = aJCas.getJFSIndexRepository().getAllIndexedFS(AtomicQueryConcept.type);

    while (it.hasNext()) {
      AtomicQueryConcept term = (AtomicQueryConcept) it.next();

      // Put the atomic queries in a list.
      List<AtomicQueryConcept> terms = new ArrayList<AtomicQueryConcept>();
      terms.add(term);

      // Generate a query operator.
      QueryOperator op = new QueryOperator(aJCas);
      op.setName("AND");

      // Create the complex query.
      ComplexQueryConcept query = new ComplexQueryConcept(aJCas);
      query.setOperatorArgs(Utils.fromCollectionToFSList(aJCas, terms));
      query.setOperator(op);
      query.addToIndexes();
    }

  }

}
