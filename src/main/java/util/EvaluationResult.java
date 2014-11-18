package util;

public class EvaluationResult {
  
  private double precision;
  private double recall;
  private double fMeasure;
  private double avgPrec;
  
  public EvaluationResult(double p, double r, double fm, double ap) {
    precision = p;
    recall = r;
    fMeasure = fm;
    avgPrec = ap;
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

}
