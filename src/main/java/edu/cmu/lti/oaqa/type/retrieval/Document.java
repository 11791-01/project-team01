

/* First created by JCasGen Sat Oct 18 19:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.type.retrieval;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



import org.apache.uima.jcas.cas.FSList;


/** A document search result.
 * Updated by JCasGen Tue Nov 18 23:51:11 EST 2014
 * XML source: /home/fei/Projects/java_workspace/project-team01/src/main/resources/type/OAQATypes.xml
 * @generated */
public class Document extends SearchResult {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Document.class);
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
  protected Document() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Document(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Document(JCas jcas) {
    super(jcas);
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
  //* Feature: title

  /** getter for title - gets The title of the document.
   * @generated
   * @return value of the feature 
   */
  public String getTitle() {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "edu.cmu.lti.oaqa.type.retrieval.Document");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Document_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets The title of the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTitle(String v) {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "edu.cmu.lti.oaqa.type.retrieval.Document");
    jcasType.ll_cas.ll_setStringValue(addr, ((Document_Type)jcasType).casFeatCode_title, v);}    
   
    
  //*--------------*
  //* Feature: docId

  /** getter for docId - gets A unique identifier for this document.
   * @generated
   * @return value of the feature 
   */
  public String getDocId() {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_docId == null)
      jcasType.jcas.throwFeatMissing("docId", "edu.cmu.lti.oaqa.type.retrieval.Document");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Document_Type)jcasType).casFeatCode_docId);}
    
  /** setter for docId - sets A unique identifier for this document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocId(String v) {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_docId == null)
      jcasType.jcas.throwFeatMissing("docId", "edu.cmu.lti.oaqa.type.retrieval.Document");
    jcasType.ll_cas.ll_setStringValue(addr, ((Document_Type)jcasType).casFeatCode_docId, v);}    
   
    
  //*--------------*
  //* Feature: fullTextAvailable

  /** getter for fullTextAvailable - gets whether the full text is available
   * @generated
   * @return value of the feature 
   */
  public boolean getFullTextAvailable() {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_fullTextAvailable == null)
      jcasType.jcas.throwFeatMissing("fullTextAvailable", "edu.cmu.lti.oaqa.type.retrieval.Document");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((Document_Type)jcasType).casFeatCode_fullTextAvailable);}
    
  /** setter for fullTextAvailable - sets whether the full text is available 
   * @generated
   * @param v value to set into the feature 
   */
  public void setFullTextAvailable(boolean v) {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_fullTextAvailable == null)
      jcasType.jcas.throwFeatMissing("fullTextAvailable", "edu.cmu.lti.oaqa.type.retrieval.Document");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((Document_Type)jcasType).casFeatCode_fullTextAvailable, v);}    
   
    
  //*--------------*
  //* Feature: abstract

  /** getter for abstract - gets The abstract of the document.
   * @generated
   * @return value of the feature 
   */
  public String getAbstract() {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_abstract == null)
      jcasType.jcas.throwFeatMissing("abstract", "edu.cmu.lti.oaqa.type.retrieval.Document");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Document_Type)jcasType).casFeatCode_abstract);}
    
  /** setter for abstract - sets The abstract of the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAbstract(String v) {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_abstract == null)
      jcasType.jcas.throwFeatMissing("abstract", "edu.cmu.lti.oaqa.type.retrieval.Document");
    jcasType.ll_cas.ll_setStringValue(addr, ((Document_Type)jcasType).casFeatCode_abstract, v);}    
   
    
  //*--------------*
  //* Feature: sentenceList

  /** getter for sentenceList - gets If full text available, this is the list of sentences of the full text.
   * @generated
   * @return value of the feature 
   */
  public FSList getSentenceList() {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_sentenceList == null)
      jcasType.jcas.throwFeatMissing("sentenceList", "edu.cmu.lti.oaqa.type.retrieval.Document");
    return (FSList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Document_Type)jcasType).casFeatCode_sentenceList)));}
    
  /** setter for sentenceList - sets If full text available, this is the list of sentences of the full text. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentenceList(FSList v) {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_sentenceList == null)
      jcasType.jcas.throwFeatMissing("sentenceList", "edu.cmu.lti.oaqa.type.retrieval.Document");
    jcasType.ll_cas.ll_setRefValue(addr, ((Document_Type)jcasType).casFeatCode_sentenceList, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    