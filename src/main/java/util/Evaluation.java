package util;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import json.gson.OutputQuestion;
import json.gson.Question;
import json.gson.Snippet;
import json.gson.TestSet;
import json.gson.TestYesNoQuestion;
import json.gson.Triple;

/**
 * The Evaluation class, used to do evaluation.
 * 
 * @author Jeremy, Anurag <alnu@cs.cmu.edu>
 *
 */
public class Evaluation {
  List<Question> goldStandard; // gold standard
  List<Question> testOutput; // output to be evaluated against gold standard
  
  String evalOutputFile = "project-team01.eval";
  String evalDetailOutputFile = "project-team01.eval-details";
  
  StringBuilder eval_sb;
  StringBuilder details_sb;

  /**
   * Constructor for creating an evaluation object with a particular gold data file
   * 
   * @param goldDataPath Path to the gold data file
   */
  public Evaluation(String goldDataPath) {
    
    try {
      // Get gold standard
      if (goldDataPath != null && goldDataPath.trim().length() != 0) {
        goldStandard = TestSet.load(getClass().getResourceAsStream(goldDataPath)).stream()
                .collect(toList());
        // trim question texts
        goldStandard.stream().filter(input -> input.getBody() != null)
                .forEach(input -> input.setBody(input.getBody().trim().replaceAll("\\s+", " ")));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    
  }
  
  /**
   * Performs evaluation on test output. Originally intended to have as input the path
   * to the test output file, so that evaluation could be done at any time on any output. 
   * Wasn't able to load it correctly from here though. So now the test output is just 
   * passed in as List of OutputQuestion objects.
   * 
   * Calls methods to perform evaluation on Concept, Document, Triple, and Snippet retrieval,
   * as well as answer generation for each question. Then calls methods to perform aggregate 
   * analysis of the metrics used for evaluation over all questions. 
   * 
   * @param testOutput List of OutputQuestions
   */
  public void doEvaluation(List<OutputQuestion> testOutput) {

    details_sb = new StringBuilder();
    eval_sb = new StringBuilder();
    
    // Get test output
//    try {
//      if (testDataPath != null && testDataPath.trim().length() != 0) {
//        testOutput = TestSet.load(Evaluation.class.getClassLoader().getResourceAsStream(testDataPath)).stream()
//                .collect(toList());
//        // trim question texts
//        testOutput.stream().filter(input -> input.getBody() != null)
//                .forEach(input -> input.setBody(input.getBody().trim().replaceAll("\\s+", " ")));
//      }
//    } catch (Exception ex) {
//      ex.printStackTrace();
//    }
  
    List<EvaluationResult> conceptsEval = new ArrayList<EvaluationResult>();
    List<EvaluationResult> documentsEval = new ArrayList<EvaluationResult>();
    List<EvaluationResult> triplesEval = new ArrayList<EvaluationResult>();
    List<EvaluationResult> snippetsEval = new ArrayList<EvaluationResult>();
    List<EvaluationResult> answersEval = new ArrayList<EvaluationResult>();
    
    try {
    // evaluate each question one at a time
    for (Question testQ : testOutput) {
      details_sb.append(String.format("Question: %s%n", testQ.getBody()));
      String qid = testQ.getId();
      Question goldQ = findGoldQuestion(qid);
      if (goldQ != null) {
        conceptsEval.add(doConceptsEval(goldQ.getConcepts(), testQ.getConcepts()));
        documentsEval.add(doDocumentsEval(goldQ.getDocuments(), testQ.getDocuments()));
        triplesEval.add(doTriplesEval(goldQ.getTriples(), testQ.getTriples()));
        snippetsEval.add(doSnippetsEval(goldQ.getSnippets(), testQ.getSnippets()));
        EvaluationResult ansResult = doAnswersEval(goldQ, testQ);
        if (ansResult != null)
          answersEval.add(ansResult);
      }
      details_sb.append("===================================================================\n");
    }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    
    eval_sb.append(String.format("%12s%12s%12s%12s%12s%12s\n", "Ret. Item", "Prec","Recall","F-measure","MAP","GMAP"));
    eval_sb.append("----------------------------------------------------------------------------\n");

    double[] conceptsMeans = calcMeanMetrics(conceptsEval);
    eval_sb.append(String.format("%12s%12.5f%12.5f%12.5f%12.5f%12.5f\n", "Concepts", conceptsMeans[0], conceptsMeans[1], 
            conceptsMeans[2], conceptsMeans[3], conceptsMeans[4]));
    double[] docMeans = calcMeanMetrics(documentsEval);
    eval_sb.append(String.format("%12s%12.5f%12.5f%12.5f%12.5f%12.5f\n", "Documents", docMeans[0], docMeans[1], 
            docMeans[2], docMeans[3], docMeans[4]));
    double[] tripMeans = calcMeanMetrics(triplesEval);
    eval_sb.append(String.format("%12s%12.5f%12.5f%12.5f%12.5f%12.5f\n", "Triples", tripMeans[0], tripMeans[1], 
            tripMeans[2], tripMeans[3], tripMeans[4]));
    double[] snipMeans = calcMeanMetrics(snippetsEval);
    eval_sb.append(String.format("%12s%12.5f%12.5f%12.5f%12.5f%12.5f\n", "Snippets", snipMeans[0], snipMeans[1], 
            snipMeans[2], snipMeans[3], snipMeans[4]));
    
    eval_sb.append(String.format("%nAnswers:%n"));
    eval_sb.append(String.format("\tYesNo - Accuracy: %8.5f%n", calcAnswerMetrics(answersEval)));
    
    FileOp.writeToFile(evalOutputFile, eval_sb.toString());
    FileOp.writeToFile(evalDetailOutputFile, details_sb.toString());
    
  }
  
  /**
   * Finds the question in the gold standard list from a question id.
   * @param qid Question ID
   * @return Gold standard Question
   */
  public Question findGoldQuestion(String qid) {
    for (Question q : goldStandard) {
      if (qid.equals(q.getId())) {
        return q;
      }
    }
    return null;
  }
  
  /**
   * Public method for Concept evaluation. Calls private version with
   * gold standard concepts based on question ID.
   * 
   * @param qid Question ID
   * @param test List of concepts to evaluate
   * @return
   */
  public EvaluationResult doConceptsEval(String qid, List<String> test) {
    Question gold = findGoldQuestion(qid);
    if (gold != null)
      return doConceptsEval(gold.getConcepts(), test);
    return null;
  }
   
  /**
   * Private method for Concept evaluation. Calls methods to calculate Precision,
   * Recall, F-measure, and Avg. Precision. 
   * @param gold List of gold standard concepts
   * @param test List of concepts to evaluate
   * @return
   */
  private EvaluationResult doConceptsEval(List<String> gold, List<String> test) {
    details_sb.append("Concepts:\n");
    printEvalDetails(gold, test);
    double precision = calcPrecision(gold, test);
    double recall = calcRecall(gold, test);
    double fmeasure = calcFMeasure(precision, recall);
    double ap = calcAP(gold, test);
    return new EvaluationResult(precision, recall, fmeasure, ap);
  }
  
  /**
   * Public method for Document evaluation. Calls private version with
   * gold standard documents based on question ID.
   * @param qid Question ID
   * @param test List of documents to evaluate
   * @return
   */
  public EvaluationResult doDocumentsEval(String qid, List<String> test) {
    Question gold = findGoldQuestion(qid);
    if (gold != null)
      return doDocumentsEval(gold.getDocuments(), test);
    return null;
  }
  
  /**
   * Private method for Document evaluation. Calls methods to calculate Precision,
   * Recall, F-measure, and Avg. Precision. 
   * @param gold List of gold standard documents
   * @param test List of documents to evaluate
   * @return
   */
  private EvaluationResult doDocumentsEval(List<String> gold, List<String> test) {
    details_sb.append("Documents:\n");
    printEvalDetails(gold, test);
    double precision = calcPrecision(gold, test);
    double recall = calcRecall(gold, test);
    double fmeasure = calcFMeasure(precision, recall);
    double ap = calcAP(gold, test);
    return new EvaluationResult(precision, recall, fmeasure, ap);
  }
  
  /**
   * Public method for Triple evaluation. Calls private version with
   * gold standard triples based on question ID.
   * @param qid Question ID
   * @param test List of triples to evaluate
   * @return
   */
  public EvaluationResult doTriplesEval(String qid, List<Triple> test) {
    Question gold = findGoldQuestion(qid);
    if (gold != null)
      return doTriplesEval(gold.getTriples(), test);
    return null;
  }
  
  /**
   * Private method for Triple evaluation. Calls methods to calculate Precision,
   * Recall, F-measure, and Avg. Precision. 
   * @param gold List of gold standard triples
   * @param test List of triples to evaluate
   * @return
   */
  private EvaluationResult doTriplesEval(List<Triple> gold, List<Triple> test) {
    details_sb.append("Triples:\n");
    printEvalDetails(gold, test);
    double precision = calcPrecision(gold, test);
    double recall = calcRecall(gold, test);
    double fmeasure = calcFMeasure(precision, recall);
    double ap = calcAP(gold, test);
    return new EvaluationResult(precision, recall, fmeasure, ap);
  }

  /**
   * Public method for Snippet evaluation. Calls private version with
   * gold standard snippets based on question ID.
   * @param qid Question ID
   * @param test List of snippets to evaluate
   * @return
   */
  public EvaluationResult doSnippetsEval(String qid, List<Snippet> test) {
    Question gold = findGoldQuestion(qid);
    if (gold != null)
      return doSnippetsEval(gold.getSnippets(), test);
    return null;
  }
  
  /**
   * Private method for Snippet evaluation. Calls methods to calculate Precision,
   * Recall, F-measure, and Avg. Precision. 
   * @param gold List of gold standard snippets
   * @param test List of snippets to evaluate
   * @return
   */
  private EvaluationResult doSnippetsEval(List<Snippet> gold, List<Snippet> test) {
    details_sb.append("Snippets:\n");
    printSnippetEvalDetails(gold, test);
    double precision = calcSnippetPrecision(gold, test);
    double recall = calcSnippetRecall(gold, test);
    double fmeasure = calcFMeasure(precision, recall);
    double ap = calcSnippetAP(gold, test);
    return new EvaluationResult(precision, recall, fmeasure, ap);
  }
  
  /**
   * Public method for Answer evaluation. Calls private version with
   * gold standard Question based on question ID.
   * @param qid Question ID
   * @param test Question to evaluate
   * @return
   */
  public EvaluationResult doAnswersEval(String qid, Question test) {
    Question gold = findGoldQuestion(qid);
    if (gold != null)
      return doAnswersEval(gold, test);
    return null;
  }
  
  /**
   * Private method for Answer evaluation. Only evaluates Yes/No questions.
   * Checks whether or not the answer is correct. 
   * @param gold Gold standard Question
   * @param test Question to evaluate
   * @return
   */
  private EvaluationResult doAnswersEval(Question gold, Question test) {
    details_sb.append("Answer:\n");
    if (gold instanceof TestYesNoQuestion) {
      String goldAnswer = ((TestYesNoQuestion)gold).getExactAnswer();
      goldAnswer = goldAnswer.replaceAll("[^a-zA-Z ]", "").toLowerCase();
      String testAnswer = ((OutputQuestion)test).getExactAnswer();
      details_sb.append(String.format("\tGold: %s%n", goldAnswer));
      details_sb.append(String.format("\tTest: %s%n", testAnswer));
      return new EvaluationResult(goldAnswer.equals(testAnswer));
    }
    return null;
  }
  
  /** 
   * Helper method to return an empty list if passed a null value.
   * @param list
   * @return
   */
  private <T> List<T> emptyListIfNull(List<T> list) {
    if (list == null)
      return new ArrayList<T>();
    return list;
  }
  
  /**
   * Precision Takes retrieved and true values as list and computes the precision. Generic can
   * handle all types
   * 
   * @param trueval
   *          ground truth list
   * @param retval
   *          retrieved list
   * @return precision
   */
  private <T> double calcPrecision(List<T> trueval, List<T> retval) {

    trueval = emptyListIfNull(trueval);
    retval = emptyListIfNull(retval);
    Set<T> trueset = new HashSet<T>(trueval);
    Set<T> retset = new HashSet<T>(retval);

    retset.retainAll(trueset);

    Integer TP = retset.size();

    if (retval.size() == 0) {
      return 0;
    }
    return ((double) TP) / ((double) retval.size());

  }
  
  /**
   * Calculates the Snippet precision for a particular question, based on
   * article-offset pairs of characters in the snippet.
   * @param gold List of gold snippets
   * @param test List of snippets to evaluate
   * @return
   */
  private double calcSnippetPrecision(List<Snippet> gold, List<Snippet> test) {
    
    gold = emptyListIfNull(gold);
    test = emptyListIfNull(test);
    
    if (test.size() == 0)
      return 0;
    
    int overlap = calcSnippetOverlapAmount(gold, test);
    
    int testSize = 0;
    for (Snippet snip : test)
      testSize += ( snip.getOffsetInEndSection() - snip.getOffsetInBeginSection() );
 
    return ((double) overlap) / testSize;
  }
  
  /**
   * Calculates the Snippet recall for a particular question, based on
   * article-offset pairs of characters in the snippet.
   * @param gold List of gold snippets
   * @param test List of snippets to evaluate
   * @return
   */
  private double calcSnippetRecall(List<Snippet> gold, List<Snippet> test) {
    
    gold = emptyListIfNull(gold);
    test = emptyListIfNull(test);
    
    if (gold.size() == 0)
      return 0;
    
    int overlap = calcSnippetOverlapAmount(gold, test);
    
    int goldSize = 0;
    for (Snippet snip : gold)
      goldSize += ( snip.getOffsetInEndSection() - snip.getOffsetInBeginSection() );
 
    return ((double) overlap) / goldSize;
  }
  
  /**
   * Calculates the Snippet average precision for a particular question.
   * @param gold List of gold standard snippets
   * @param test List of snippets to evaluate
   * @return
   */
  private double calcSnippetAP(List<Snippet> gold, List<Snippet> test) {
    
    int poscount = 0;
    double ap = 0.0;
    int totalOverlap = 0;
    int totalTest = 0;
    

    for (Snippet testSnip : test) {
      //find gold snips matching sect/doc
      List<Snippet> goldMatches = new ArrayList<Snippet>();
      for (Snippet s : gold) {
        if (s.getDocument().equals(testSnip.getDocument()) && s.getBeginSection().equals(testSnip.getBeginSection()))
          goldMatches.add(s);
      }
      totalTest += testSnip.getOffsetInEndSection() - testSnip.getOffsetInBeginSection();
      //calculate overlap
      for (Snippet goldSnip : goldMatches) {
        int overlapBegin = Math.max(testSnip.getOffsetInBeginSection(), goldSnip.getOffsetInBeginSection());
        int overlapEnd = Math.min(testSnip.getOffsetInEndSection(), goldSnip.getOffsetInEndSection());
        if (overlapBegin < overlapEnd) {
          totalOverlap += (overlapEnd - overlapBegin);
          poscount += 1;
          ap += (totalOverlap / ((double) (totalTest)));
        }
      }
    }
    return ap / (poscount + Math.pow(10, -15));
  }
  
  /**
   * Calculates the amount of overlap in characters between two lists of 
   * snippets. This is used for calculating precision/recall.
   * @param gold
   * @param test
   * @return
   */
  private int calcSnippetOverlapAmount(List<Snippet> gold, List<Snippet> test) {
    
    int overlapAmt = 0;
    for (Snippet testSnip : test) {
      //find gold snips matching sect/doc
      List<Snippet> goldMatches = new ArrayList<Snippet>();
      for (Snippet s : gold) {
        if (s.getDocument().equals(testSnip.getDocument()) && s.getBeginSection().equals(testSnip.getBeginSection()))
          goldMatches.add(s);
      }
      //calculate overlap
      for (Snippet goldSnip : goldMatches) {
        int overlapBegin = Math.max(testSnip.getOffsetInBeginSection(), goldSnip.getOffsetInBeginSection());
        int overlapEnd = Math.min(testSnip.getOffsetInEndSection(), goldSnip.getOffsetInEndSection());
        if (overlapBegin < overlapEnd) {
          overlapAmt += (overlapEnd - overlapBegin);
        }
      }
    }
    return overlapAmt;
  }

  /**
   * Recall Takes retrieved and true values as list and computes the precision. Generic can handle
   * all types
   * 
   * @param trueval
   *          ground truth list
   * @param retval
   *          retrieved list
   * @return recall
   */
  private <T> double calcRecall(List<T> trueval, List<T> retval) {

    trueval = emptyListIfNull(trueval);
    retval = emptyListIfNull(retval);
    Set<T> trueset = new HashSet<T>(trueval);
    Set<T> retset = new HashSet<T>(retval);

    retset.retainAll(trueset);

    Integer TP = retset.size();

    if (trueval.size() == 0) {
      return 0;
    }
    return ((double) TP) / ((double) trueval.size());

  }

  /**
   * F-measure Takes retrieved and true values as list and computes the f-measure.
   * 
   * @param prec
   *          precision
   * @param rec
   *          recall
   * @return F-Measure
   */
  private double calcFMeasure(Double prec, Double rec) {

    if (prec + rec == 0) {
      return 0;
    }
    return (2 * prec * rec) / (prec + rec);

  }

  /**
   * Average Precision Takes retrieved and true values as list and computes the precision. Generic
   * can handle all types
   * 
   * @param trueval
   *          ground truth list
   * @param retval
   *          retrieved list
   * @return Average Precision
   */
  private <T> double calcAP(List<T> trueval, List<T> retval) {

    trueval = emptyListIfNull(trueval);
    retval = emptyListIfNull(retval);
    int poscount = 0;
    double ap = 0.0;
    int c = 0;
    for (T item : retval) {

      if (trueval.contains(item)) {
        poscount += 1;
        ap += (poscount / ((double) (c + 1)));
      }
      c = c + 1;
    }

    return ap / (poscount + Math.pow(10, -15));

  }
  
  /**
   * Calculates the means of the Precisions, Recalls, F-measures, and Avg. Precisions,
   * over a list of questions.
   * @param evals List of Evaluation results for the questions
   * @return
   */
  private double[] calcMeanMetrics(List<EvaluationResult> evals) {
    double epsilon = 0.001;
    double meanPrec = 0;
    double meanRec = 0;
    double meanFmeas = 0;
    double MAP = 0;
    double GMAP = 1;
    int numQues = evals.size();
    
    for (EvaluationResult eval : evals) {
      meanPrec += eval.getPrecision();
      meanRec += eval.getRecall();
      meanFmeas += eval.getfMeasure();
      MAP += eval.getAvgPrec();
      GMAP *= (eval.getAvgPrec() + epsilon);
    }
    return new double[] { meanPrec / numQues, meanRec / numQues, meanFmeas / numQues, 
            MAP / numQues, Math.pow(GMAP, 1.0 / numQues) };
  }
  
  /**
   * Calculates the Accuracy over a list of questions
   * @param evals List of Evaluation results for the questions
   * @return
   */
  private double calcAnswerMetrics(List<EvaluationResult> evals) {

    double accuracy = 0;
    int numQues = evals.size();
    
    for (EvaluationResult eval : evals) {
      accuracy += (eval.getIsCorrect() ? 1 : 0);
    }
    
    return accuracy / numQues;
  }
  
  /**
   * Prints the details of the evaluation for Concepts, Docs, and Triples.
   * That is, the lists of True Positives, False Positives, and False Negatives.
   * @param gold 
   * @param test
   */
  private <T> void printEvalDetails(List<T> gold, List<T> test) {
    
    gold = emptyListIfNull(gold);
    test = emptyListIfNull(test);
    
    Set<T> matches = new HashSet<T>();
    matches.addAll(gold);
    matches.retainAll(test);
    details_sb.append("\tTrue Pos:\n");
    matches.forEach(m -> details_sb.append(String.format("\t\t%s%n", m.toString())));
    
    Set<T> testOnly = new HashSet<T>(test);
    testOnly.removeAll(gold);
    details_sb.append("\tFalse Pos:\n");
    testOnly.forEach(m -> details_sb.append(String.format("\t\t%s%n", m.toString())));
    
    Set<T> goldOnly = new HashSet<T>(gold);
    goldOnly.removeAll(test);
    details_sb.append("\tFalse Neg:\n");
    goldOnly.forEach(m -> details_sb.append(String.format("\t\t%s%n", m.toString())));
  }
  
  /**
   * Prints the details of the evaluation for Snippets.
   * Prints both the gold and test versions of overlapping snippets. Also
   * prints the False Positive and False Negative snippets.
   * @param gold
   * @param test
   */
  private void printSnippetEvalDetails(List<Snippet> gold, List<Snippet> test) {
    
    gold = emptyListIfNull(gold);
    test = emptyListIfNull(test);
    
    int overlapAmt = 0;
    List<Snippet[]> matches = new ArrayList<Snippet[]>();
    List<Snippet> testMatches = new ArrayList<Snippet>();
    List<Snippet> goldMatches = new ArrayList<Snippet>();
    for (Snippet testSnip : test) {
      //find gold snips matching sect/doc
      List<Snippet> goldSameDocSection = new ArrayList<Snippet>();
      for (Snippet s : gold) {
        if (s.getDocument().equals(testSnip.getDocument()) && s.getBeginSection().equals(testSnip.getBeginSection()))
          goldSameDocSection.add(s);
      }
      //find snips that overlap
      for (Snippet goldSnip : goldSameDocSection) {
        int overlapBegin = Math.max(testSnip.getOffsetInBeginSection(), goldSnip.getOffsetInBeginSection());
        int overlapEnd = Math.min(testSnip.getOffsetInEndSection(), goldSnip.getOffsetInEndSection());
        if (overlapBegin < overlapEnd) {
          overlapAmt += (overlapEnd - overlapBegin);
          matches.add(new Snippet[] {testSnip, goldSnip});
          testMatches.add(testSnip);
          goldMatches.add(goldSnip);
        }
      }
    }
      

    details_sb.append("\tTrue Pos:\n");
    matches.forEach(m -> details_sb.append(String.format("\t\tTest: |%s, %s-%s, %d-%d|%n\t\t\t%s%n\t\tGold: |%s, %s-%s, %d-%d|%n\t\t\t%s%n-%n",
            m[0].getDocument(), m[0].getBeginSection(), m[0].getEndSection(), m[0].getOffsetInBeginSection(), m[0].getOffsetInEndSection(), m[0].getText(), 
            m[1].getDocument(), m[1].getBeginSection(), m[1].getEndSection(), m[1].getOffsetInBeginSection(), m[1].getOffsetInEndSection(), m[1].getText())));
    
    Set<Snippet> testOnly = new HashSet<Snippet>(test);
    testOnly.removeAll(testMatches);
    details_sb.append("\tFalse Pos:\n");
    testOnly.forEach(m -> details_sb.append(String.format("\t\t%s%n", m.getText())));
    
    Set<Snippet> goldOnly = new HashSet<Snippet>(gold);
    goldOnly.removeAll(goldMatches);
    details_sb.append("\tFalse Neg:\n");
    goldOnly.forEach(m -> details_sb.append(String.format("\t\t%s%n", m.getText())));
    
  }
}
