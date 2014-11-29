package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.spell.TfIdfDistance;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.StopTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

import edu.cmu.lti.oaqa.type.retrieval.Document;

public class SimCalculator {

  private static SimCalculator instance = null;

  private TokenizerFactory REFINED_TKFACTORY = null;

  private SimCalculator() {
  }

  public static SimCalculator getInstance() {
    if (instance == null) {
      instance = new SimCalculator();
    }

    return instance;
  }

  public void init(String stopFilePath) throws ResourceInitializationException {
    // init lingpipe tokenization factory
    String content = FileOp.getFileAsStream(stopFilePath, SimCalculator.class);
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
  }

  public List<Double> tfidfScore(String queryWOOp, List<Document> docList) {
    TfIdfDistance tfIdf = new TfIdfDistance(REFINED_TKFACTORY);

    tfIdf.handle(queryWOOp);
    for (Document doc : docList) {
      tfIdf.handle(doc.getAbstract());
    }
    
    List<Double> scoreList = new ArrayList<Double>();
    for (Document doc : docList) {
      double score = tfIdf.proximity(queryWOOp, doc.getAbstract());
      scoreList.add(score);
    }

    return scoreList;
  }
}
