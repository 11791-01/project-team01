package edu.cmu.lti.deiis.project.annotator;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;

import util.NLParser;
import edu.cmu.lti.oaqa.type.answer.Answer;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.Passage;

/**
 * The answer annotator, used to generate the exact answer for yes/no questions
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 */

public class AnswerAnnotator extends JCasAnnotator_ImplBase {

  /**
   * The nlp parser used to detect positive and negative
   */
  NLParser nlparser;

  /**
   * Perform initialization logic. Initialize the service.
   * 
   * @param aContext
   *          the UimaContext object
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    try {
      nlparser = new NLParser();
    } catch (Exception ex) {
      throw new ResourceInitializationException();
    }
  }

  /**
   * Process to get the exact answer
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Question.type).iterator();

    // check the question type
    Question question = (Question) iter.next();
    String type = question.getQuestionType();
    if (!type.equals("YES_NO")) {
      return;
    }

    // positive and negative detection
    FSIterator<TOP> snippetIter = aJCas.getJFSIndexRepository().getAllIndexedFS(Passage.type);
    List<Boolean> yesnoList = new ArrayList<Boolean>();
    while (snippetIter.hasNext()) {
      Passage snippet = (Passage) snippetIter.next();
      String text = snippet.getText();
      boolean yesno = nlparser.doParse(text);
      yesnoList.add(yesno);
    }

    // voting
    int yesNum = 0, noNum = 0;
    for (int i = 0; i < yesnoList.size(); ++i) {
      if (yesnoList.get(i)) {
        ++yesNum;
      } else {
        ++noNum;
      }
    }
    System.out.println("");

    String ansStr = "yes";
    if (yesNum >= noNum) {
      ansStr = "yes";
    } else {
      ansStr = "no";
    }
    
    if (yesnoList.size() == 0) {
      ansStr = "no";
    }

    // add to cas
    Answer answer = new Answer(aJCas);
    answer.setText(ansStr);
    answer.addToIndexes();
    System.out.println(yesNum + ", " + noNum);
    System.out.println(ansStr);
  }

}
