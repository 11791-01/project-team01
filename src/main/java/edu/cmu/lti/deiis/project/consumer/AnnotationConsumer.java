/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.cmu.lti.deiis.project.consumer;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import json.gson.Question;
import json.gson.TestSet;
import json.gson.TrainingSet;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.collection.base_cpm.CasObjectProcessor;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import com.google.common.collect.Sets;

import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;
import util.FileOp;
import util.TripleHelper;


/**
 * AnnotationConsumer prints to an output file the gene entity annotation in the CAS. <br>
 * Parameters needed by the AnnotationConsumer are
 * <ol>
 * <li>"outputFile" : file to which the output files should be written.</li>
 * <li>"goldDataFile": Optional. file of goldData, used to do evaluation</li>
 * </ol>
 * <br>
 * These parameters are set in the initialize method to the values specified in the descriptor file. <br>
 * These may also be set by the application by using the setConfigParameterValue methods.
 * 
 * 
 */

public class AnnotationConsumer extends CasConsumer_ImplBase implements CasObjectProcessor {
  /*
   * output file path
   */
  String oPath;

  /*
   * gold dataset file path
   */
  String evalDataPath;

  /*
   * The StringBuilder used to concatenate the output string
   */
  StringBuilder sb;
  
  Boolean ifeval;
  
  List<Question> goldout;
  List<Double[]> precisions;
  //Lirst<Double>[] precisions;
  List<Double[]> recalls;
  List<Double[]> fmeasures;
  List<Double[]> AvgPrecisions;

  public AnnotationConsumer() {
  }

  /**
   * Initializes this CAS Consumer with the parameters specified in the descriptor.
   * 
   * @throws ResourceInitializationException
   *           if there is error in initializing the resources
   */
  public void initialize() throws ResourceInitializationException {

    // extract configuration parameter settings
    oPath = (String) getUimaContext().getConfigParameterValue("outputFile");

    // Output file should be specified in the descriptor
    if (oPath == null) {
      throw new ResourceInitializationException(
              ResourceInitializationException.CONFIG_SETTING_ABSENT, new Object[] { "outputFile" });
    }

    // If specified output directory does not exist, try to create it
    File outFile = new File(oPath.trim());
    if (outFile.getParentFile() != null && !outFile.getParentFile().exists()) {
      if (!outFile.getParentFile().mkdirs())
        throw new ResourceInitializationException(
                ResourceInitializationException.RESOURCE_DATA_NOT_VALID, new Object[] { oPath,
                    "outputFile" });
    }

    evalDataPath = (String) getUimaContext().getConfigParameterValue("goldDataFile");
    
    if (evalDataPath != null && evalDataPath.trim().length() != 0) {
      File theFile = new File(evalDataPath.trim());
      
      if (theFile.exists() && !theFile.isDirectory()) {
        //Evaluation eval = new Evaluation(sb.toString(), FileOp.readFromFile(evalDataPath.trim()));
        //eval.evaluate();
        ifeval = true ;
        
        
        goldout = TestSet.load(getClass().getResourceAsStream(evalDataPath)).stream().collect(toList());
        // trim question texts
        goldout.stream().filter(input -> input.getBody() != null)
                .forEach(input -> input.setBody(input.getBody().trim().replaceAll("\\s+", " ")));
        
      }
      else 
        ifeval = false ;
    }
    
    
    
    precisions =new ArrayList<Double[]>();
    recalls = new ArrayList<Double[]>();
    fmeasures = new ArrayList<Double[]>();
    AvgPrecisions = new ArrayList<Double[]>();
    sb = new StringBuilder();
  }

  /**
   * Processes the CasContainer which was populated by the TextAnalysisEngines. <br>
   * In this case, the CAS index is iterated over selected annotations and append the 
   * relevant information to StringBuilder sb
   * 
   * @param aCAS
   *          CasContainer which has been populated by the TAEs
   * 
   * @throws ResourceProcessException
   *           if there is an error in processing the Resource
   * 
   * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(CAS)
   */
  public synchronized void processCas(CAS aCAS) throws ResourceProcessException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }
    
    // do sb append here.
    
    
    
    
    FSIndex<?> ConceptIndex = jcas.getAnnotationIndex(ConceptSearchResult.type);
    Iterator<?> ConceptIter = ConceptIndex.iterator();
    Map<Integer,String> conceptmaps = new TreeMap<Integer,String>();  
    while(ConceptIter.hasNext()){
      
      ConceptSearchResult cpt = (ConceptSearchResult) ConceptIter.next(); 
      conceptmaps.put(cpt.getRank(),cpt.getUri());
      
    }
    
    FSIndex<?> DocIndex = jcas.getAnnotationIndex(Document.type);
    Iterator<?> DocIter = DocIndex.iterator();
    Map<Integer,String> docmaps = new TreeMap<Integer,String>();  
    while(DocIter.hasNext()){
      
      Document doc  = (Document) DocIter.next(); 
      docmaps.put(doc.getRank(),doc.getUri());
      
    }
    
    FSIndex<?> trpIndex = jcas.getAnnotationIndex(TripleSearchResult.type);
    Iterator<?> TrpIter = trpIndex.iterator();
    Map<Integer,TripleHelper> trpmaps = new TreeMap<Integer,TripleHelper>();  
    while(TrpIter.hasNext()){
      
      TripleSearchResult trp = (TripleSearchResult) TrpIter.next();
      Triple temp = trp.getTriple();
      trpmaps.put(trp.getRank(),new TripleHelper(temp.getSubject(), temp.getPredicate(), temp.getObject()));
      
    }
    
    
    FSIndex<?> QuestionIndex = jcas.getAnnotationIndex(edu.cmu.lti.oaqa.type.input.Question.type);
    Iterator<?> QuestionIter = QuestionIndex.iterator();
    Question question = (Question) QuestionIter.next();
    
    List<Integer> gtlabel = new ArrayList<Integer>();
    List<String> gtconcepts = new ArrayList<String>();
    List<String> gtdocs = new ArrayList<String>();
    List<TripleHelper> gttrpls = new ArrayList<TripleHelper>();
    
      
      String qid = question.getId();
      for(Question cqst:goldout){
        
        if(qid==cqst.getId()){
          gtconcepts=cqst.getConcepts();
          gtdocs=cqst.getDocuments();
          List<json.gson.Triple> tempTrips = cqst.getTriples();
          for (json.gson.Triple t : tempTrips) {
            gttrpls.add(new TripleHelper(t.getS(), t.getP(), t.getO()));
          }
        }
      }
    
    
    //COmpute All Values Precision, Recall, AP, F-Score
    Double[] qprec = new Double[3];
    qprec[0]=precision(gtconcepts,(List<String>)conceptmaps.values());
    qprec[1]=precision(gtdocs,(List<String>)docmaps.values());
    qprec[2]=precision(gttrpls,(List<TripleHelper>)trpmaps.values());
    
    precisions.add(qprec);
    
    Double[] qrec = new Double[3];
    qrec[0]=recall(gtconcepts,(List<String>)conceptmaps.values());
    qrec[1]=recall(gtdocs,(List<String>)docmaps.values());
    qrec[2]=recall(gttrpls,(List<TripleHelper>)trpmaps.values());
    
    recalls.add(qprec);
    
    Double[] qfms = new Double[3];
    qfms[0]=fmeasure(qprec[0],qrec[0]);
    qfms[1]=fmeasure(qprec[1],qrec[1]);
    qfms[2]=fmeasure(qprec[2],qrec[2]);
    
    fmeasures.add(qfms);
    
    
    
    Double[] qap = new Double[3];
    qap[0]=AP(gtconcepts,(List<String>)conceptmaps.values());
    qap[1]=AP(gtdocs,(List<String>)docmaps.values());
    qap[2]=AP(gttrpls,(List<TripleHelper>)trpmaps.values());
    
    AvgPrecisions.add(qap);
    
    
  }

  private <T> Double precision(List<T> trueval,List<T>retval){
    
    Set<T> trueset = new HashSet<T>(trueval);
    Set<T> retset  = new HashSet<T>(retval);
    
    retset.retainAll(trueset);
    
    Integer TP  = retset.size();
    
    return ((double)TP)/((double)retval.size());
        
  }
  
 private <T> Double recall(List<T> trueval,List<T>retval){
    
    Set<T> trueset = new HashSet<T>(trueval);
    Set<T> retset  = new HashSet<T>(retval);
    
    retset.retainAll(trueset);
    //List<String> intersect = 
    //for (int i=0;i<trueval.size();i++)
    
    Integer TP  = retset.size();
    
    return ((double)TP)/((double)trueval.size());
        
  }
  
 private Double fmeasure(Double prec,Double rec){
   
   return (2*prec*rec)/(prec+rec);
       
 }
  
 private <T> Double AP(List<T> trueval,List<T>retval){
   
   int poscount = 0;
   Double ap = 0.0;
   int c=0;
   for (T item:retval){
     
     if(trueval.contains(item)){
       poscount+=1;
       ap+=(poscount/(c+1));
     }
     c=c+1;
   }
   
   
   
   return ap/poscount;
       
 }
 
 
 
  //private static Double computeAP(ArrayList<Double>,ArrayList<Integer>){
    
  //}
  /**
   * Called when a batch of processing is completed.
   * 
   * @param aTrace
   *          ProcessTrace object that will log events in this method.
   * @throws ResourceProcessException
   *           if there is an error in processing the Resource
   * @throws IOException
   *           if there is an IO Error
   * 
   * @see org.apache.uima.collection.CasConsumer#batchProcessComplete(ProcessTrace)
   */
  public void batchProcessComplete(ProcessTrace aTrace) throws ResourceProcessException,
          IOException {
    // nothing to do in this case as AnnotationPrinter doesnot do
    // anything cumulatively
  }

  /**
   * Called when the entire collection is completed. Write the string to file.
   * 
   * @param aTrace
   *          ProcessTrace object that will log events in this method.
   * @throws ResourceProcessException
   *           if there is an error in processing the Resource
   * @throws IOException
   *           if there is an IO Error
   * @see org.apache.uima.collection.CasConsumer#collectionProcessComplete(ProcessTrace)
   */
  public void collectionProcessComplete(ProcessTrace aTrace) throws ResourceProcessException,
          IOException {
    //FileOp.writeToFile(oPath, sb.toString());

    // if evaluation path exists, do the evaluation
    Double[] MAPs=new Double[3];
    Double[] meanprecs=new Double[3];
    Double[] meanfmss=new Double[3];
    Double[] meanrecs=new Double[3];
    int numQues=precisions.size();
    
    for(int i=0;i<precisions.size();i++){
      
      for(int j=0;j<3;j++){
        
        MAPs[j]+=AvgPrecisions.get(i)[j];
        meanprecs[j]+=precisions.get(i)[j];
        meanrecs[j]+=recalls.get(i)[j];
        meanfmss[j]+=fmeasures.get(i)[j];
        
      } 
    }
    for (int i=0;i<3;i++){
      MAPs[i]/=numQues;
      meanfmss[i]/=numQues;
      meanrecs[i]/=numQues;
      meanprecs[i]/=numQues;
    }
    
  }
  
  

  /**
   * Called if clean up is needed in case of exit under error conditions.
   * 
   * @see org.apache.uima.resource.Resource#destroy()
   */
  public void destroy() {
  }
}
