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
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class Evaluation {
  List<Question> goldStandard; // gold standard
  List<Question> testOutput; // output to be evaluated against gold standard
  
  String evalOutputFile = "project-team01.eval";
  String evalDetailOutputFile = "project-team01.eval-details";
  
  StringBuilder eval_sb;
  StringBuilder details_sb;

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
//        answersEval.add(doAnswersEval(goldQ, testQ));
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
    
    FileOp.writeToFile(evalOutputFile, eval_sb.toString());
    FileOp.writeToFile(evalDetailOutputFile, details_sb.toString());
    
  }
  
  public Question findGoldQuestion(String qid) {
    for (Question q : goldStandard) {
      if (qid.equals(q.getId())) {
        return q;
      }
    }
    return null;
  }
  
  public EvaluationResult doConceptsEval(String qid, List<String> test) {
    Question gold = findGoldQuestion(qid);
    if (gold != null)
      return doConceptsEval(gold.getConcepts(), test);
    return null;
  }
  private EvaluationResult doConceptsEval(List<String> gold, List<String> test) {
    details_sb.append("Concepts:\n");
    printEvalDetails(gold, test);
    double precision = calcPrecision(gold, test);
    double recall = calcRecall(gold, test);
    double fmeasure = calcFMeasure(precision, recall);
    double ap = calcAP(gold, test);
    return new EvaluationResult(precision, recall, fmeasure, ap);
  }
  
  public EvaluationResult doDocumentsEval(String qid, List<String> test) {
    Question gold = findGoldQuestion(qid);
    if (gold != null)
      return doDocumentsEval(gold.getDocuments(), test);
    return null;
  }
  private EvaluationResult doDocumentsEval(List<String> gold, List<String> test) {
    details_sb.append("Documents:\n");
    printEvalDetails(gold, test);
    double precision = calcPrecision(gold, test);
    double recall = calcRecall(gold, test);
    double fmeasure = calcFMeasure(precision, recall);
    double ap = calcAP(gold, test);
    return new EvaluationResult(precision, recall, fmeasure, ap);
  }
  
  public EvaluationResult doTriplesEval(String qid, List<Triple> test) {
    Question gold = findGoldQuestion(qid);
    if (gold != null)
      return doTriplesEval(gold.getTriples(), test);
    return null;
  }
  private EvaluationResult doTriplesEval(List<Triple> gold, List<Triple> test) {
    details_sb.append("Triples:\n");
    printEvalDetails(gold, test);
    double precision = calcPrecision(gold, test);
    double recall = calcRecall(gold, test);
    double fmeasure = calcFMeasure(precision, recall);
    double ap = calcAP(gold, test);
    return new EvaluationResult(precision, recall, fmeasure, ap);
  }

  public EvaluationResult doSnippetsEval(String qid, List<Snippet> test) {
    Question gold = findGoldQuestion(qid);
    if (gold != null)
      return doSnippetsEval(gold.getSnippets(), test);
    return null;
  }
  private EvaluationResult doSnippetsEval(List<Snippet> gold, List<Snippet> test) {
    details_sb.append("Snippets:\n");
    printSnippetEvalDetails(gold, test);
    double precision = calcSnippetPrecision(gold, test);
    double recall = calcSnippetRecall(gold, test);
    double fmeasure = calcFMeasure(precision, recall);
    double ap = 0;
    return new EvaluationResult(precision, recall, fmeasure, ap);
  }
  public EvaluationResult doAnswersEval(String qid, String test) {
    Question gold = findGoldQuestion(qid);
    if (gold != null)
      return doAnswersEval(gold, test);
    return null;
  }
  
  private EvaluationResult doAnswersEval(Question gold, String test) {
    details_sb.append("Answer:\n");
//    printSnippetEvalDetails(gold, test);
    if (gold instanceof TestYesNoQuestion) {
      return new EvaluationResult(((TestYesNoQuestion)gold).getExactAnswer() == test);
    }
    return null;
  }
  
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
  private <T> Double calcAP(List<T> trueval, List<T> retval) {

    trueval = emptyListIfNull(trueval);
    retval = emptyListIfNull(retval);
    int poscount = 0;
    Double ap = 0.0;
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
  

  private double[] calcMeanMetrics(List<EvaluationResult> evals) {
    double epsilon = 0.001;
    double meanPrec = 0;
    double meanRec = 0;
    double meanFmeas = 0;
    double MAP = 0;
    double GMAP = 1;
    double accuracy = 0;
    int numQues = evals.size();
    
    for (EvaluationResult eval : evals) {
      meanPrec += eval.getPrecision();
      meanRec += eval.getRecall();
      meanFmeas += eval.getfMeasure();
      MAP += eval.getAvgPrec();
      GMAP *= (eval.getAvgPrec() + epsilon);
      accuracy += (eval.getIsCorrect() ? 1 : 0);
    }
    return new double[] { meanPrec / numQues, meanRec / numQues, meanFmeas / numQues, 
            MAP / numQues, Math.pow(GMAP, 1.0 / numQues), accuracy };
  }
  
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
    matches.forEach(m -> details_sb.append(String.format("\t\tTest: %s%n\t\tGold: %s%n-%n", m[0].getText(), m[1].getText())));
    
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
