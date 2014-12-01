package edu.cmu.lti.deiis.project.reranker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.resource.ResourceInitializationException;

import util.FileOp;
import util.MyComp;
import util.SimCalculator;

import com.aliasi.spell.TfIdfDistance;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.StopTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

import edu.cmu.lti.deiis.project.assitance.RetrType;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.Document;

/**
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class DocReranker extends JCasAnnotator_ImplBase {

  /**
   * Perform initialization logic. Initialize the service.
   * 
   * @param aContext
   *          the UimaContext object
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<TOP> DocIter = aJCas.getJFSIndexRepository().getAllIndexedFS(Document.type);
    FSIterator<TOP> queryIter = aJCas.getJFSIndexRepository().getAllIndexedFS(
            ComplexQueryConcept.type);

    ComplexQueryConcept query = (ComplexQueryConcept) queryIter.next();
    String queryWOOp = query.getWholeQueryWithoutOp();
    System.out.println("Doc Reranking...");

    List<Document> docList = new ArrayList<Document>();
    while (DocIter.hasNext()) {
      docList.add((Document)DocIter.next());
    }
    
    SimCalculator simCalcInst = SimCalculator.getInstance();
    List<Double> scoreList = simCalcInst.tfidfScore(queryWOOp, docList, RetrType.DOC);
    for (int i = 0; i < scoreList.size(); ++i) {
      double score = scoreList.get(i);
      docList.get(i).setScore(score);
    }

    Collections.sort(docList, new MyComp.DocSimComparator());
    for (int i = 0; i < docList.size(); ++i) {
      docList.get(i).setRank(i);
    }
  }
}
