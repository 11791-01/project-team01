package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import json.gson.Question;
import json.gson.Triple;

public class Evaluation {
  List<Question> goldout;

  List<Double[]> precisions;

  List<Double[]> recalls;

  List<Double[]> fmeasures;

  List<Double[]> AvgPrecisions;

  public Evaluation(List<Question> gold) {
    goldout = gold;
    precisions = new ArrayList<Double[]>();
    recalls = new ArrayList<Double[]>();
    fmeasures = new ArrayList<Double[]>();
    AvgPrecisions = new ArrayList<Double[]>();
  }

  public void evalOneQuestion(String qid, List<String> retDocs, List<String> retConcepts,
          List<Triple> retTriples) {
    List<String> gtconcepts = new ArrayList<String>();
    List<String> gtdocs = new ArrayList<String>();
    List<Triple> gttrpls = new ArrayList<Triple>();

    for (Question cqst : goldout) {

      if (qid.equals(cqst.getId())) {
        gtconcepts = cqst.getConcepts();
        gtdocs = cqst.getDocuments();
        List<Triple> tempTrips = cqst.getTriples();
        if (tempTrips != null) {
          for (Triple t : tempTrips) {
            gttrpls.add(new Triple(t.getS(), t.getP(), t.getO()));
          }
        }
      }
    }

    // Compute All Values Precision, Recall, AP, F-Score
    Double[] qprec = new Double[3];
    qprec[0] = precision(gtconcepts, retConcepts);
    qprec[1] = precision(gtdocs, retDocs);
    qprec[2] = precision(gttrpls, retTriples);

    precisions.add(qprec);

    Double[] qrec = new Double[3];
    qrec[0] = recall(gtconcepts, retConcepts);
    qrec[1] = recall(gtdocs, retDocs);
    qrec[2] = recall(gttrpls, retTriples);

    recalls.add(qrec);

    Double[] qfms = new Double[3];
    qfms[0] = fmeasure(qprec[0], qrec[0]);
    qfms[1] = fmeasure(qprec[1], qrec[1]);
    qfms[2] = fmeasure(qprec[2], qrec[2]);

    fmeasures.add(qfms);

    Double[] qap = new Double[3];
    qap[0] = AP(gtconcepts, retConcepts);
    qap[1] = AP(gtdocs, retDocs);
    qap[2] = AP(gttrpls, retTriples);

    AvgPrecisions.add(qap);
    System.out.println("************");
    System.out.println("GTDocs:");
    for (String c : gtconcepts) {
      System.out.println("\t" + c);
    }
    System.out.println("RetDocs:");
    for (String c : retConcepts) {
      System.out.println("\t" + c);
    }

    System.out.println("conc" + qprec[0] + "  doc" + qprec[1] + "  trps" + qprec[2]);
    System.out.println("conc" + qrec[0] + "  doc" + qrec[1] + "  trps" + qrec[2]);
    System.out.println("************");
  }

  public void evalAllQuestion() {
    double[] MAPs = new double[3];
    double[] GMAPs = { 1, 1, 1 };
    double[] meanprecs = new double[3];
    double[] meanfmss = new double[3];
    double[] meanrecs = new double[3];
    int numQues = precisions.size();

    for (int i = 0; i < precisions.size(); i++) {
      for (int j = 0; j < 3; j++) {
        GMAPs[j] *= (AvgPrecisions.get(i)[j] + 0.00001);
        MAPs[j] += AvgPrecisions.get(i)[j];
        meanprecs[j] += precisions.get(i)[j];
        meanrecs[j] += recalls.get(i)[j];
        meanfmss[j] += fmeasures.get(i)[j];

      }
    }

    System.out.println("Prec\tRecall\tF-measure\tMAP\tGMAP");
    for (int i = 0; i < 3; i++) {
      MAPs[i] /= numQues;
      meanfmss[i] /= numQues;
      meanrecs[i] /= numQues;
      meanprecs[i] /= numQues;
      GMAPs[i] = Math.pow(GMAPs[i], 1.0 / numQues);
      System.out.printf("%.4f\t%.4f\t%.4f\t%.4f\t%.4f\n", meanprecs[i], meanrecs[i], meanfmss[i],
              MAPs[i], GMAPs[i]);
    }
  }

  private <T> double precision(List<T> trueval, List<T> retval) {

    Set<T> trueset = new HashSet<T>(trueval);
    Set<T> retset = new HashSet<T>(retval);

    retset.retainAll(trueset);

    Integer TP = retset.size();

    if (retval.size() == 0) {
      return 0;
    }
    return ((double) TP) / ((double) retval.size());

  }

  private <T> double recall(List<T> trueval, List<T> retval) {

    Set<T> trueset = new HashSet<T>(trueval);
    Set<T> retset = new HashSet<T>(retval);

    retset.retainAll(trueset);

    Integer TP = retset.size();

    if (trueval.size() == 0) {
      return 0;
    }
    return ((double) TP) / ((double) trueval.size());

  }

  private double fmeasure(Double prec, Double rec) {

    if (prec + rec == 0) {
      return 0;
    }
    return (2 * prec * rec) / (prec + rec);

  }

  private <T> Double AP(List<T> trueval, List<T> retval) {

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
}
