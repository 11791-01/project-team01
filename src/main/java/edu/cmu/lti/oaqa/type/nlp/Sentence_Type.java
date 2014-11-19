
/* First created by JCasGen Tue Nov 18 23:51:11 EST 2014 */
package edu.cmu.lti.oaqa.type.nlp;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Nov 18 23:51:11 EST 2014
 * @generated */
public class Sentence_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Sentence_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Sentence_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Sentence(addr, Sentence_Type.this);
  			   Sentence_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Sentence(addr, Sentence_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Sentence.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.type.nlp.Sentence");
 
  /** @generated */
  final Feature casFeat_beginSection;
  /** @generated */
  final int     casFeatCode_beginSection;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getBeginSection(int addr) {
        if (featOkTst && casFeat_beginSection == null)
      jcas.throwFeatMissing("beginSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    return ll_cas.ll_getStringValue(addr, casFeatCode_beginSection);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBeginSection(int addr, String v) {
        if (featOkTst && casFeat_beginSection == null)
      jcas.throwFeatMissing("beginSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    ll_cas.ll_setStringValue(addr, casFeatCode_beginSection, v);}
    
  
 
  /** @generated */
  final Feature casFeat_endSection;
  /** @generated */
  final int     casFeatCode_endSection;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getEndSection(int addr) {
        if (featOkTst && casFeat_endSection == null)
      jcas.throwFeatMissing("endSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    return ll_cas.ll_getStringValue(addr, casFeatCode_endSection);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEndSection(int addr, String v) {
        if (featOkTst && casFeat_endSection == null)
      jcas.throwFeatMissing("endSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    ll_cas.ll_setStringValue(addr, casFeatCode_endSection, v);}
    
  
 
  /** @generated */
  final Feature casFeat_offsetInBeginSection;
  /** @generated */
  final int     casFeatCode_offsetInBeginSection;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getOffsetInBeginSection(int addr) {
        if (featOkTst && casFeat_offsetInBeginSection == null)
      jcas.throwFeatMissing("offsetInBeginSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    return ll_cas.ll_getIntValue(addr, casFeatCode_offsetInBeginSection);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOffsetInBeginSection(int addr, int v) {
        if (featOkTst && casFeat_offsetInBeginSection == null)
      jcas.throwFeatMissing("offsetInBeginSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    ll_cas.ll_setIntValue(addr, casFeatCode_offsetInBeginSection, v);}
    
  
 
  /** @generated */
  final Feature casFeat_offsetInEndSection;
  /** @generated */
  final int     casFeatCode_offsetInEndSection;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getOffsetInEndSection(int addr) {
        if (featOkTst && casFeat_offsetInEndSection == null)
      jcas.throwFeatMissing("offsetInEndSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    return ll_cas.ll_getIntValue(addr, casFeatCode_offsetInEndSection);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOffsetInEndSection(int addr, int v) {
        if (featOkTst && casFeat_offsetInEndSection == null)
      jcas.throwFeatMissing("offsetInEndSection", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    ll_cas.ll_setIntValue(addr, casFeatCode_offsetInEndSection, v);}
    
  
 
  /** @generated */
  final Feature casFeat_score;
  /** @generated */
  final int     casFeatCode_score;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getScore(int addr) {
        if (featOkTst && casFeat_score == null)
      jcas.throwFeatMissing("score", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_score);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setScore(int addr, double v) {
        if (featOkTst && casFeat_score == null)
      jcas.throwFeatMissing("score", "edu.cmu.lti.oaqa.type.nlp.Sentence");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_score, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Sentence_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_beginSection = jcas.getRequiredFeatureDE(casType, "beginSection", "uima.cas.String", featOkTst);
    casFeatCode_beginSection  = (null == casFeat_beginSection) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_beginSection).getCode();

 
    casFeat_endSection = jcas.getRequiredFeatureDE(casType, "endSection", "uima.cas.String", featOkTst);
    casFeatCode_endSection  = (null == casFeat_endSection) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_endSection).getCode();

 
    casFeat_offsetInBeginSection = jcas.getRequiredFeatureDE(casType, "offsetInBeginSection", "uima.cas.Integer", featOkTst);
    casFeatCode_offsetInBeginSection  = (null == casFeat_offsetInBeginSection) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_offsetInBeginSection).getCode();

 
    casFeat_offsetInEndSection = jcas.getRequiredFeatureDE(casType, "offsetInEndSection", "uima.cas.Integer", featOkTst);
    casFeatCode_offsetInEndSection  = (null == casFeat_offsetInEndSection) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_offsetInEndSection).getCode();

 
    casFeat_score = jcas.getRequiredFeatureDE(casType, "score", "uima.cas.Double", featOkTst);
    casFeatCode_score  = (null == casFeat_score) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_score).getCode();

  }
}



    