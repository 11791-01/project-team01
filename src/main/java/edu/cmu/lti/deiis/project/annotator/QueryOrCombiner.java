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
 * An annotator used to combine the atomic queries into a complex query with the use of AND and OR
 * operator.
 * 
 * @author Zexi Mao <zexim@cs.cmu.edu>
 *
 */
public class QueryOrCombiner extends JCasAnnotator_ImplBase {

  /**
   * Reads all atomic queries and put them into one complext query.
   * 
   * @see
   * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<TOP> it = aJCas.getJFSIndexRepository().getAllIndexedFS(AtomicQueryConcept.type);

    // Put the atomic queries in a list.
    List<AtomicQueryConcept> terms = new ArrayList<AtomicQueryConcept>();
    List<String> words = new ArrayList<String>();

    // Generate AND query operator.
    QueryOperator opAnd = new QueryOperator(aJCas);
    opAnd.setName("AND");

    // Generate OR query operator.
    QueryOperator opOr = new QueryOperator(aJCas);
    opOr.setName("OR");
    
    // Create the whole query strings
    StringBuilder wholeWithOp = new StringBuilder();
    StringBuilder wholeWithoutOp = new StringBuilder();
    
    while (it.hasNext()) {
      AtomicQueryConcept term = (AtomicQueryConcept) it.next();
      terms.add(term);
      words.add(term.getText());
    }
    
    for (int i = 0; i < words.size(); i++) {
      if (i != 0) {
        wholeWithoutOp.append(" ");
      }
      wholeWithoutOp.append(words.get(i));
      for (int j = i+1; j < words.size(); j++) {
        if (j != 1) {
          wholeWithOp.append(" " + opOr.getName() + " ");
        }
        wholeWithOp.append("(" + words.get(i) + " " + opAnd.getName() + " " + words.get(j) + ")");
      }
    }
    
    // Create the complex query.
    ComplexQueryConcept query = new ComplexQueryConcept(aJCas);
    query.setOperatorArgs(Utils.fromCollectionToFSList(aJCas, terms));
    query.setOperator(opOr);
    query.setWholeQueryWithOp(wholeWithOp.toString());
    query.setWholeQueryWithoutOp(wholeWithoutOp.toString());
    query.addToIndexes();
  }

}
