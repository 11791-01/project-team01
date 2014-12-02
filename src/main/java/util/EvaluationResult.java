package util;

/**
 * A class to contain the results of evaluation for a particular retrieval item
 * on a particular question. It has fields for various metrics used in evaluation.
 * 
 * @author jeremy
 *
 */
public class EvaluationResult {
  
  private double precision;
  private double recall;
  private double fMeasure;
  private double avgPrec;
  private boolean isCorrect;
  
  /**
   * Constructor for metrics pertaining to retrieval (concepts, docs, triples, snippets).
   * 
   * @param p Precision
   * @param r Recall
   * @param fm f-measure
   * @param ap Average precision
   */
  public EvaluationResult(double p, double r, double fm, double ap) {
    precision = p;
    recall = r;
    fMeasure = fm;
    avgPrec = ap;
  }
  
  /**
   * Constructor for metrics pertaining to yes/no answer generation.
   * 
   * @param isCorrect Whether or not the answer is correct
   */
  public EvaluationResult(boolean isCorrect) {
    this.isCorrect = isCorrect;
  }

  public double getPrecision() {
    return precision;
  }

  public void setPrecision(double precision) {
    this.precision = precision;
  }

  public double getRecall() {
    return recall;
  }

  public void setRecall(double recall) {
    this.recall = recall;
  }

  public double getfMeasure() {
    return fMeasure;
  }

  public void setfMeasure(double fMeasure) {
    this.fMeasure = fMeasure;
  }

  public double getAvgPrec() {
    return avgPrec;
  }

  public void setAvgPrec(double avgPrec) {
    this.avgPrec = avgPrec;
  }

  public boolean getIsCorrect() {
    return isCorrect;
  }

  public void setIsCorrect(boolean isCorrect) {
    this.isCorrect = isCorrect;
  }

}
