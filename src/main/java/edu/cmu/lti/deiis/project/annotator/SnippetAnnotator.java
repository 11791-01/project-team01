package edu.cmu.lti.deiis.project.annotator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.chrono.MinguoChronology;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.spell.TfIdfDistance;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.StopTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import util.MyComp;
import util.SimCalculator;
import util.WebServiceHelper;
import edu.cmu.lti.deiis.project.assitance.RawSentence;
import edu.cmu.lti.deiis.project.assitance.RetrType;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.nlp.Sentence;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.retrieval.Passage;

/**
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class SnippetAnnotator extends JCasAnnotator_ImplBase {
  private SentenceChunker SENTENCE_CHUNKER;

  /**
   * Perform initialization logic. Initialize the service.
   * 
   * @param aContext
   *          the UimaContext object
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    TokenizerFactory BASE_TKFACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
    SentenceModel SENTENCE_MODEL = new MedlineSentenceModel();
    SENTENCE_CHUNKER = new SentenceChunker(BASE_TKFACTORY, SENTENCE_MODEL);
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<TOP> DocIter = aJCas.getJFSIndexRepository().getAllIndexedFS(Document.type);
    FSIterator<TOP> queryIter = aJCas.getJFSIndexRepository().getAllIndexedFS(
            ComplexQueryConcept.type);
    ComplexQueryConcept query = (ComplexQueryConcept) queryIter.next();
    String queryWOOp = query.getWholeQueryWithoutOp();
    System.out.println("Snippet Retrieval...");

    while (DocIter.hasNext()) {
      Document doc = (Document) DocIter.next();
      String pmid = doc.getDocId();

      JsonObject jsonObj = WebServiceHelper.getJsonFromPMID(pmid);

      String sec0 = null;
      if (jsonObj != null) {
        JsonArray secArr = jsonObj.getAsJsonArray("sections");
        sec0 = secArr.get(0).getAsString();
      } else {
        sec0 = doc.getAbstract();
        //continue;
      }

      Chunking chunking = SENTENCE_CHUNKER.chunk(sec0.toCharArray(), 0, sec0.length());
      List<Chunk> sentences = new ArrayList<Chunk>(chunking.chunkSet());

      List<RawSentence> rawSentences = new ArrayList<RawSentence>();
      for (int i = 0; i < sentences.size(); ++i) {
        Chunk sentence = sentences.get(i);
        int start = sentence.start();
        int end = sentence.end();
        String text = sec0.substring(start, end);
        rawSentences.add(new RawSentence(start, end, text));
      }

      SimCalculator simCalcInst = SimCalculator.getInstance();
      List<Double> scoreList = simCalcInst.tfidfScore(queryWOOp, rawSentences, RetrType.RAW_SENT);
      for (int i = 0; i < scoreList.size(); ++i) {
        double score = scoreList.get(i);
        rawSentences.get(i).setScore(score);
      }

      Collections.sort(rawSentences, new MyComp.SenSimComparator());

      int threshold = Math.min(5, sentences.size());
      for (int i = 0; i < threshold; ++i) {
        Passage snippet = new Passage(aJCas);
        int startIdx = rawSentences.get(i).getStartIdx();
        int endIdx = rawSentences.get(i).getEndIdx();
        String tmpText = sec0.substring(startIdx, endIdx);
        if (tmpText.contains("a larger gene number")) {
          FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Question.type).iterator();
          System.out.println(((Question)iter.next()).getText());
          System.out.println(doc.getUri());
          System.out.println(startIdx + " " + endIdx);
          System.out.println(tmpText);
        }
        snippet.setDocId(pmid);
        snippet.setUri(doc.getUri());
        snippet.setRank(i);
        snippet.setText(sec0.substring(startIdx, endIdx));
        snippet.setBeginSection("sections.0");
        snippet.setEndSection("sections.0");
        snippet.setOffsetInBeginSection(startIdx);
        snippet.setOffsetInEndSection(endIdx);
        snippet.addToIndexes();
      }
    }

    System.out.println("Snippet Retrieval Finished!");
  }

}