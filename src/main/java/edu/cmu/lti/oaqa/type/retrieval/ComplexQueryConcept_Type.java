
/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.retrieval;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** A hierarchical query concept represented by a query operation on a list of concepts
 * Updated by JCasGen Tue Nov 18 02:36:40 EST 2014
 * @generated */
public class ComplexQueryConcept_Type extends QueryConcept_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ComplexQueryConcept_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ComplexQueryConcept_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ComplexQueryConcept(addr, ComplexQueryConcept_Type.this);
  			   ComplexQueryConcept_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ComplexQueryConcept(addr, ComplexQueryConcept_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ComplexQueryConcept.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept");
 
  /** @generated */
  final Feature casFeat_operator;
  /** @generated */
  final int     casFeatCode_operator;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getOperator(int addr) {
        if (featOkTst && casFeat_operator == null)
      jcas.throwFeatMissing("operator", "edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept");
    return ll_cas.ll_getRefValue(addr, casFeatCode_operator);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOperator(int addr, int v) {
        if (featOkTst && casFeat_operator == null)
      jcas.throwFeatMissing("operator", "edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept");
    ll_cas.ll_setRefValue(addr, casFeatCode_operator, v);}
    
  
 
  /** @generated */
  final Feature casFeat_operatorArgs;
  /** @generated */
  final int     casFeatCode_operatorArgs;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getOperatorArgs(int addr) {
        if (featOkTst && casFeat_operatorArgs == null)
      jcas.throwFeatMissing("operatorArgs", "edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept");
    return ll_cas.ll_getRefValue(addr, casFeatCode_operatorArgs);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOperatorArgs(int addr, int v) {
        if (featOkTst && casFeat_operatorArgs == null)
      jcas.throwFeatMissing("operatorArgs", "edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept");
    ll_cas.ll_setRefValue(addr, casFeatCode_operatorArgs, v);}
    
  
 
  /** @generated */
  final Feature casFeat_wholeQueryWithOp;
  /** @generated */
  final int     casFeatCode_wholeQueryWithOp;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getWholeQueryWithOp(int addr) {
        if (featOkTst && casFeat_wholeQueryWithOp == null)
      jcas.throwFeatMissing("wholeQueryWithOp", "edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept");
    return ll_cas.ll_getStringValue(addr, casFeatCode_wholeQueryWithOp);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setWholeQueryWithOp(int addr, String v) {
        if (featOkTst && casFeat_wholeQueryWithOp == null)
      jcas.throwFeatMissing("wholeQueryWithOp", "edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept");
    ll_cas.ll_setStringValue(addr, casFeatCode_wholeQueryWithOp, v);}
    
  
 
  /** @generated */
  final Feature casFeat_wholeQueryWithoutOp;
  /** @generated */
  final int     casFeatCode_wholeQueryWithoutOp;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getWholeQueryWithoutOp(int addr) {
        if (featOkTst && casFeat_wholeQueryWithoutOp == null)
      jcas.throwFeatMissing("wholeQueryWithoutOp", "edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept");
    return ll_cas.ll_getStringValue(addr, casFeatCode_wholeQueryWithoutOp);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setWholeQueryWithoutOp(int addr, String v) {
        if (featOkTst && casFeat_wholeQueryWithoutOp == null)
      jcas.throwFeatMissing("wholeQueryWithoutOp", "edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept");
    ll_cas.ll_setStringValue(addr, casFeatCode_wholeQueryWithoutOp, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ComplexQueryConcept_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_operator = jcas.getRequiredFeatureDE(casType, "operator", "edu.cmu.lti.oaqa.type.retrieval.QueryOperator", featOkTst);
    casFeatCode_operator  = (null == casFeat_operator) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_operator).getCode();

 
    casFeat_operatorArgs = jcas.getRequiredFeatureDE(casType, "operatorArgs", "uima.cas.FSList", featOkTst);
    casFeatCode_operatorArgs  = (null == casFeat_operatorArgs) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_operatorArgs).getCode();

 
    casFeat_wholeQueryWithOp = jcas.getRequiredFeatureDE(casType, "wholeQueryWithOp", "uima.cas.String", featOkTst);
    casFeatCode_wholeQueryWithOp  = (null == casFeat_wholeQueryWithOp) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_wholeQueryWithOp).getCode();

 
    casFeat_wholeQueryWithoutOp = jcas.getRequiredFeatureDE(casType, "wholeQueryWithoutOp", "uima.cas.String", featOkTst);
    casFeatCode_wholeQueryWithoutOp  = (null == casFeat_wholeQueryWithoutOp) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_wholeQueryWithoutOp).getCode();

  }
}



    