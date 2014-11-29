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
import util.SimCalculator;

import com.aliasi.spell.TfIdfDistance;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.StopTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.Document;

/**
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class DocReranker extends JCasAnnotator_ImplBase {

  /*public static final String PARAM_STOP_WORD_FILE = "StopWordFile";

  private TokenizerFactory REFINED_TKFACTORY = null;*/

  /**
   * Perform initialization logic. Initialize the service.
   * 
   * @param aContext
   *          the UimaContext object
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    /*String stopFilePath = (String) aContext.getConfigParameterValue(PARAM_STOP_WORD_FILE);
    String content = FileOp.getFileAsStream(stopFilePath, DocReranker.class);
    String[] lines = content.split("\n");
    Set<String> tmpSet = new HashSet<String>();
    for (String line : lines) {
      tmpSet.add(line);
    }
    Set<String> stopSet = Collections.unmodifiableSet(tmpSet);

    REFINED_TKFACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
    REFINED_TKFACTORY = new StopTokenizerFactory(REFINED_TKFACTORY, stopSet);
    REFINED_TKFACTORY = new LowerCaseTokenizerFactory(REFINED_TKFACTORY);
    REFINED_TKFACTORY = new PorterStemmerTokenizerFactory(REFINED_TKFACTORY);
    */
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<TOP> DocIter = aJCas.getJFSIndexRepository().getAllIndexedFS(Document.type);
    FSIterator<TOP> queryIter = aJCas.getJFSIndexRepository().getAllIndexedFS(
            ComplexQueryConcept.type);

    ComplexQueryConcept query = (ComplexQueryConcept) queryIter.next();
    String queryWOOp = query.getWholeQueryWithoutOp();
    System.out.println("Doc Reranking...");

    /*TfIdfDistance tfIdf = new TfIdfDistance(REFINED_TKFACTORY);
    tfIdf.handle(queryWOOp);

    List<Document> docList = new ArrayList<Document>();
    while (DocIter.hasNext()) {
      Document doc = (Document) DocIter.next();
      if (doc.getAbstract() == null || doc.getAbstract().trim().length() == 0) {
        System.out.println("-----------------------------");
        continue;
      }

      tfIdf.handle(doc.getAbstract());

      docList.add(doc);
    }

    for (int i = 0; i < docList.size(); ++i) {
      Document doc = docList.get(i);
      double sim = tfIdf.proximity(queryWOOp, docList.get(i).getAbstract());
      doc.setScore(sim);
    }*/
    List<Document> docList = new ArrayList<Document>();
    while (DocIter.hasNext()) {
      docList.add((Document)DocIter.next());
    }
    
    List<Double> scoreList = SimCalculator.getInstance().tfidfScore(queryWOOp, docList);
    for (int i = 0; i < scoreList.size(); ++i) {
      double score = scoreList.get(i);
      docList.get(i).setScore(score);
    }

    Collections.sort(docList, new DocSimComparator());
    for (int i = 0; i < docList.size(); ++i) {
      docList.get(i).setRank(i);
    }
  }
}

class DocSimComparator implements Comparator<Document> {
  @Override
  public int compare(Document lhs, Document rhs) {
    if (lhs.getScore() < rhs.getScore()) {
      return 1;
    } else if (lhs.getScore() > rhs.getScore()) {
      return -1;
    } else {
      return lhs.getRank() - rhs.getRank();
    }
  }
}
