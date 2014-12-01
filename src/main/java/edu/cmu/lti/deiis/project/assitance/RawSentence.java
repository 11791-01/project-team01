package edu.cmu.lti.deiis.project.assitance;

/**
 * @author Fei Xia <feixia@cs.cmu.edu>
 */

public class RawSentence {
  /**
   * the start index
   */
  private int startIdx;

  /**
   * the end index
   */
  private int endIdx;
  
  /**
   * The sentence text
   */
  private String text;
  
  /**
   * The doc id
   */
  private String docID;
  
  /**
   * The src URI
   */
  private String srcURI;

  /**
   * The score
   */
  private double score = 0.0;
  
  /**
   * Constructor.
   * 
   * @param startIdx the starting index
   * @param endIdx the end index
   * @param text the text
   * @param docID the doc id
   * @param srcURI the source uri
   */
  public RawSentence(int startIdx, int endIdx, String text, String docID, String srcURI) {
    this.startIdx = startIdx;
    this.endIdx = endIdx;
    this.text = text;
    this.docID = docID;
    this.srcURI = srcURI;
  }
  
  /**
   * Set the similarity score
   * 
   * @param score the score
   */
  public void setScore(double score) {
    this.score = score;
  }

  /**
   * Get the similarity score
   * @return the score
   */
  public double getScore() {
    return score;
  }
  
  /**
   * Get the text of the sentence
   * 
   * @return the sentence text
   */
  public String getText() {
    return text;
  }
  
  /**
   * Get the starting index
   * @return the start index
   */
  public int getStartIdx() {
    return startIdx;
  }
  
  /**
   * Get the ending index
   * @return the end index
   */
  public int getEndIdx() {
    return endIdx;
  }
  
  /**
   * Get the doc id
   * @return the doc id
   */
  public String getDocID() {
    return docID;
  }
  
  /**
   * Get the source uri
   * @return the source uri
   */
  public String getSrcURI() {
    return srcURI;
  }
}
