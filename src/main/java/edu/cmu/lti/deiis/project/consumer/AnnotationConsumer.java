package edu.cmu.lti.deiis.project.consumer;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import json.gson.OutputAnswer;
import json.gson.Question;
import json.gson.TestSet;
import json.gson.Triple;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.collection.base_cpm.CasObjectProcessor;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import util.Evaluation;
import util.FileOp;

import com.google.gson.Gson;

import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;

/**
 * AnnotationConsumer prints to an output file the result annotation in the CAS. <br>
 * Parameters needed by the AnnotationConsumer are
 * <ol>
 * <li>"outputFile" : file to which the output files should be written.</li>
 * <li>"goldDataFile": Optional. file of goldData, used to do evaluation</li>
 * </ol>
 * <br>
 * These parameters are set in the initialize method to the values specified in the descriptor file. <br>
 * These may also be set by the application by using the setConfigParameterValue methods.
 * 
 */

public class AnnotationConsumer extends CasConsumer_ImplBase implements CasObjectProcessor {
  /**
   * Output file path
   */
  String oPath;

  /**
   * Gold dataset file path
   */
  String evalDataPath;

  /**
   * If we need to do the evaluation or not
   */
  Boolean ifeval;

  /**
   * The retrieved answers list
   */
  List<OutputAnswer> retrievedAnswers;

  /**
   * The Evaluation
   */
  Evaluation eval;

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

    // try to get the ground truth data path
    evalDataPath = (String) getUimaContext().getConfigParameterValue("goldDataFile");
    try {
      ifeval = false;
      if (evalDataPath != null && evalDataPath.trim().length() != 0) {
        List<Question> goldout = TestSet.load(getClass().getResourceAsStream(evalDataPath)).stream()
                .collect(toList());
        // trim question texts
        goldout.stream().filter(input -> input.getBody() != null)
                .forEach(input -> input.setBody(input.getBody().trim().replaceAll("\\s+", " ")));
        ifeval = true;

        eval = new Evaluation(goldout);
      }
    } catch (Exception ex) {
      ifeval = false;
    }

    retrievedAnswers = new ArrayList<OutputAnswer>();
  }

  /**
   * Processes the CasContainer which was populated by the TextAnalysisEngines. <br>
   * In this case, the CAS index is iterated over selected annotations and append the relevant
   * information to corresponding data structure 
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
    // Getting the Retrieved Concepts, Documents and Triples
    // Iterators for all of them
    FSIndex<?> QuestionIndex = jcas.getAnnotationIndex(edu.cmu.lti.oaqa.type.input.Question.type);
    Iterator<?> QuestionIter = QuestionIndex.iterator();
    edu.cmu.lti.oaqa.type.input.Question question = (edu.cmu.lti.oaqa.type.input.Question) QuestionIter
            .next();

    // Create Tree Map for each type of Retrievals
    FSIterator<TOP> ConceptIter = jcas.getJFSIndexRepository().getAllIndexedFS(
            ConceptSearchResult.type);

    Map<Integer, String> conceptmaps = new TreeMap<Integer, String>();
    while (ConceptIter.hasNext()) {

      ConceptSearchResult cpt = (ConceptSearchResult) ConceptIter.next();
      conceptmaps.put(cpt.getRank(), cpt.getUri());

    }

    FSIterator<TOP> DocIter = jcas.getJFSIndexRepository().getAllIndexedFS(Document.type);
    Map<Integer, String> docmaps = new TreeMap<Integer, String>();
    while (DocIter.hasNext()) {

      Document doc = (Document) DocIter.next();
      docmaps.put(doc.getRank(), doc.getUri());

    }

    FSIterator<TOP> TrpIter = jcas.getJFSIndexRepository().getAllIndexedFS(TripleSearchResult.type);
    Map<Integer, Triple> trpmaps = new TreeMap<Integer, Triple>();
    while (TrpIter.hasNext()) {

      TripleSearchResult trp = (TripleSearchResult) TrpIter.next();
      edu.cmu.lti.oaqa.type.kb.Triple temp = trp.getTriple();
      trpmaps.put(trp.getRank(),
              new Triple(temp.getSubject(), temp.getPredicate(), temp.getObject()));

    }
    // Storing Results in List
    List<String> retDocs = new ArrayList<String>(docmaps.values());
    List<String> retConcepts = new ArrayList<String>(conceptmaps.values());
    List<Triple> retTriples = new ArrayList<Triple>(trpmaps.values());

    retrievedAnswers.add(new OutputAnswer(question.getId(), question.getText(), retDocs,
            retConcepts, retTriples));

    // Evaluating the current question in CAS
    // Do only if gold standard exists
    if (ifeval) {
      eval.evalOneQuestion(question.getId(), retDocs, retConcepts, retTriples);
    }
  }

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
    // nothing to do in this case as AnnotationPrinter does not do
    // anything cumulatively
  }

  /**
   * Called when the entire collection is completed. Write results to file.
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

    // Writing it as json file
    Gson gson = new Gson();
    String jsonOutput = gson.toJson(retrievedAnswers);
    FileOp.writeToFile(oPath, jsonOutput);

    // if evaluation path exists, do the evaluation
    // Evaluating Final Performance for all Questions
    if (ifeval) {
      eval.evalAllQuestion();
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
