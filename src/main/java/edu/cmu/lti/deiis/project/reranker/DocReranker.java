package edu.cmu.lti.deiis.project.reranker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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

  private String stopFilePath = "models/stopwords.txt";

  private TokenizerFactory REFINED_TKFACTORY = null;

  /**
   * Perform initialization logic. Initialize the service.
   * 
   * @param aContext
   *          the UimaContext object
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    String content = getFileAsStream(stopFilePath);
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

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<TOP> DocIter = aJCas.getJFSIndexRepository().getAllIndexedFS(Document.type);
    FSIterator<TOP> queryIter = aJCas.getJFSIndexRepository().getAllIndexedFS(
            ComplexQueryConcept.type);

    ComplexQueryConcept query = (ComplexQueryConcept) queryIter.next();
    String queryWOOp = query.getWholeQueryWithoutOp();

    TfIdfDistance tfIdf = new TfIdfDistance(REFINED_TKFACTORY);
    tfIdf.handle(queryWOOp);

    List<Document> docList = new ArrayList<Document>(); 
    while (DocIter.hasNext()) {
      Document doc = (Document) DocIter.next();
      tfIdf.handle(doc.getAbstract());
      
      docList.add(doc);
    }
    
    for (int i = 0; i < docList.size(); ++i) {
      Document doc = docList.get(i);
      double sim = tfIdf.proximity(queryWOOp, docList.get(i).getAbstract());
      doc.setScore(sim);
    }
    
    // sort the docList desc in terms of score
    // then assign the rank
  }

  /**
   * Read file through stream LPDictExactNERAnnotator
   * 
   * @param filePath
   *          the file path
   * @return the string of the file
   * @throws ResourceInitializationException
   */
  private String getFileAsStream(String filePath) throws ResourceInitializationException {
    StringBuilder sb = new StringBuilder();
    try {
      InputStream is = DocReranker.class.getClassLoader().getResourceAsStream(filePath);

      BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));

      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      br.close();
    } catch (Exception ex) {
      System.out.println("[Error]: Look Below.");
      ex.printStackTrace();
      System.out.println("[Error]: Look Above.");
      throw new ResourceInitializationException();
    }

    String content = sb.toString();
    return content;
  }

}
