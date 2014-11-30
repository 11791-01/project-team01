package edu.cmu.lti.deiis.project.assitance;

/**
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */

public class RawSentence {
  private int startIdx;

  private int endIdx;
  
  private String text;
  
  private String docID;
  
  private String srcURI;

  private double score = 0.0;
  
  public RawSentence(int startIdx, int endIdx, String text, String docID, String srcURI) {
    this.startIdx = startIdx;
    this.endIdx = endIdx;
    this.text = text;
    this.docID = docID;
    this.srcURI = srcURI;
  }
  
  public void setScore(double score) {
    this.score = score;
  }

  public double getScore() {
    return score;
  }
  
  public String getText() {
    return text;
  }
  
  public int getStartIdx() {
    return startIdx;
  }
  
  public int getEndIdx() {
    return endIdx;
  }
  
  public String getDocID() {
    return docID;
  }
  
  public String getSrcURI() {
    return srcURI;
  }
}
