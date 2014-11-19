

/* First created by JCasGen Tue Nov 18 23:51:11 EST 2014 */
package edu.cmu.lti.oaqa.type.nlp;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Nov 18 23:51:11 EST 2014
 * XML source: /home/fei/Projects/java_workspace/project-team01/src/main/resources/type/OAQATypes.xml
 * @generated */
public class Sentence extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Sentence.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Sentence() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Sentence(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Sentence(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Sentence(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: beginSection

  /** getter for beginSection - gets 
   * @generated
   * @return value of the feature 
   */
  public String getBeginSection() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_beginSection == null)
      jcasType.jcas.throwFeatMissing("beginSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_beginSection);}
    
  /** setter for beginSection - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBeginSection(String v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_beginSection == null)
      jcasType.jcas.throwFeatMissing("beginSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    jcasType.ll_cas.ll_setStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_beginSection, v);}    
   
    
  //*--------------*
  //* Feature: endSection

  /** getter for endSection - gets 
   * @generated
   * @return value of the feature 
   */
  public String getEndSection() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_endSection == null)
      jcasType.jcas.throwFeatMissing("endSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_endSection);}
    
  /** setter for endSection - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEndSection(String v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_endSection == null)
      jcasType.jcas.throwFeatMissing("endSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    jcasType.ll_cas.ll_setStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_endSection, v);}    
   
    
  //*--------------*
  //* Feature: offsetInBeginSection

  /** getter for offsetInBeginSection - gets 
   * @generated
   * @return value of the feature 
   */
  public int getOffsetInBeginSection() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_offsetInBeginSection == null)
      jcasType.jcas.throwFeatMissing("offsetInBeginSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Sentence_Type)jcasType).casFeatCode_offsetInBeginSection);}
    
  /** setter for offsetInBeginSection - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setOffsetInBeginSection(int v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_offsetInBeginSection == null)
      jcasType.jcas.throwFeatMissing("offsetInBeginSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    jcasType.ll_cas.ll_setIntValue(addr, ((Sentence_Type)jcasType).casFeatCode_offsetInBeginSection, v);}    
   
    
  //*--------------*
  //* Feature: offsetInEndSection

  /** getter for offsetInEndSection - gets 
   * @generated
   * @return value of the feature 
   */
  public int getOffsetInEndSection() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_offsetInEndSection == null)
      jcasType.jcas.throwFeatMissing("offsetInEndSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Sentence_Type)jcasType).casFeatCode_offsetInEndSection);}
    
  /** setter for offsetInEndSection - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setOffsetInEndSection(int v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_offsetInEndSection == null)
      jcasType.jcas.throwFeatMissing("offsetInEndSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    jcasType.ll_cas.ll_setIntValue(addr, ((Sentence_Type)jcasType).casFeatCode_offsetInEndSection, v);}    
   
    
  //*--------------*
  //* Feature: score

  /** getter for score - gets 
   * @generated
   * @return value of the feature 
   */
  public double getScore() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Sentence_Type)jcasType).casFeatCode_score);}
    
  /** setter for score - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setScore(double v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Sentence_Type)jcasType).casFeatCode_score, v);}    
  }

    