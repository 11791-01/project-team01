/**
 * 
 */
package edu.cmu.lti.deiis.project.annotator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import util.Utils;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.StopTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.AtomicQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;

/**
 * An annotator used to generate the queries by conducting tokenization, stopword removal, etc.
 * 
 * @author Zexi Mao <zexim@cs.cmu.edu>
 *
 */
public class LPQueryAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Name of configuration parameter that must be set to the path of the model file.
   */
  public static final String PARAM_STOP_WORD_FILE = "StopWordFile";

  // The tokenizer used for tokenization and other processing of question
  private TokenizerFactory mTokenizerFactory;

  // A set for storing the stop words
  private Set<String> mStopSet;

  /**
   * Initialize the annotator, create the TokenizerFactory in LinePipe.
   * 
   * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(UimaContext)
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    mStopSet = new HashSet<String>();
    String content = getFileAsStream((String) aContext
            .getConfigParameterValue(PARAM_STOP_WORD_FILE));
    String[] lines = content.split("\n");
    for (String line : lines) {
      mStopSet.add(line);
    }

    // Add all the features needed for the tokenizer
    mTokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
    mTokenizerFactory = new LowerCaseTokenizerFactory(mTokenizerFactory);
    mTokenizerFactory = new StopTokenizerFactory(mTokenizerFactory, mStopSet);
  }

  /**
   * Create queries for each question.
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Question.type).iterator();

    if (iter.isValid() && iter.hasNext()) {
      // Get the question first
      Question question = (Question) iter.next();
      
      // Put all the tokens into one query string
      String queString = question.getText();
      List<String> tokens = new ArrayList<String>();
      List<String> others = new ArrayList<>();
      String queryString = "";

      Tokenizer tokenizer = mTokenizerFactory.tokenizer(queString.toCharArray(), 0,
              queString.length());
      tokenizer.tokenize(tokens, others);

      for (String token : tokens) {
        queryString += token;
        queryString += " ";
      }

      // Create an atomic query first
      AtomicQueryConcept atomicQuery = new AtomicQueryConcept(aJCas);
      atomicQuery.setText(queryString);
      atomicQuery.addToIndexes();
      List<AtomicQueryConcept> terms = new ArrayList<AtomicQueryConcept>();
      terms.add(atomicQuery);

      // Create the query for the following Annotators.
      ComplexQueryConcept query = new ComplexQueryConcept(aJCas);
      query.setOperatorArgs(Utils.fromCollectionToFSList(aJCas, terms));
      query.addToIndexes();
    }
  }

  /*
   * A helper function used to read in the stop word file.
   */
  private String getFileAsStream(String filePath) throws ResourceInitializationException {
    StringBuilder sb = new StringBuilder();
    try {
      InputStream is = LPQueryAnnotator.class.getClassLoader().getResourceAsStream(filePath);

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
