package edu.cmu.lti.deiis.project.annotator;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.resource.ResourceInitializationException;

import util.Utils;
import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.retrieval.AtomicQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;

/**
 * Query to get the concept.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>, Zexi Mao <zexim@cs.cmu.edu>
 */
public class QueryConcept extends JCasAnnotator_ImplBase {

  /**
   * Name of configuration parameter that must be set to the number of returned concepts in each
   * service.
   */
  public static final String PARAM_RESULTS_PER_PAGE = "ResultsPerPage";

  private Integer mResultsPerPage;

  // The GoPubMedService
  private GoPubMedService service;

  /**
   * Perform initialization logic. Initialize the service.
   * 
   * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(UimaContext)
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    mResultsPerPage = (Integer) aContext.getConfigParameterValue(PARAM_RESULTS_PER_PAGE);

    try {
      service = new GoPubMedService("project.properties");
    } catch (Exception ex) {
      throw new ResourceInitializationException();
    }
  }

  /**
   * Get the concepts and add them to JCas index
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<TOP> queryIter = aJCas.getJFSIndexRepository().getAllIndexedFS(
            ComplexQueryConcept.type);

    try {
      // TODO: add other services and combine them to get an overall ranking

      ComplexQueryConcept query = (ComplexQueryConcept) queryIter.next();

      List<AtomicQueryConcept> queryList = (ArrayList<AtomicQueryConcept>) Utils
              .fromFSListToCollection(query.getOperatorArgs(), AtomicQueryConcept.class);
      // Get the query text
      String text = queryList.get(0).getText();
      // Use Mesh service
      OntologyServiceResponse.Result meshResult = service.findMeshEntitiesPaged(text, 0,mResultsPerPage);
            
      //Add multiple sources here
      //Combine them in some way
      
      //GoPubMedService service = new GoPubMedService("project.properties");
      OntologyServiceResponse.Result diseaseOntologyResult = service.findDiseaseOntologyEntitiesPaged(text, 0);
      
      System.out.println("Disease ontology: " + diseaseOntologyResult.getFindings().size());
      for (OntologyServiceResponse.Finding finding : diseaseOntologyResult.getFindings()) {
        System.out.println(" > " + finding.getConcept().getLabel() + " "
                + finding.getConcept().getUri());
      }
      
      
      OntologyServiceResponse.Result geneOntologyResult = service.findGeneOntologyEntitiesPaged(text,0, 10);
      //System.out.println("Gene ontology: " + geneOntologyResult.getFindings().size());
      //for (OntologyServiceResponse.Finding finding : geneOntologyResult.getFindings()) {
      //  System.out.println(" > " + finding.getConcept().getLabel() + " "
       //         + finding.getConcept().getUri());
      //}
      
      OntologyServiceResponse.Result jochemResult = service.findJochemEntitiesPaged(text, 0);
      //System.out.println("Jochem: " + jochemResult.getFindings().size());
      //for (OntologyServiceResponse.Finding finding : jochemResult.getFindings()) {
      //  System.out.println(" > " + finding.getConcept().getLabel() + " "
      //          + finding.getConcept().getUri());
      //}
      
      
      OntologyServiceResponse.Result uniprotResult = service.findUniprotEntitiesPaged(text, 0);
      //System.out.println("UniProt: " + uniprotResult.getFindings().size());
      //for (OntologyServiceResponse.Finding finding : uniprotResult.getFindings()) {
      //  System.out.println(" > " + finding.getConcept().getLabel() + " "
      //          + finding.getConcept().getUri());
      //}
      
      //Add selective sources here
      
      aJCas=addSelectedService(meshResult,text,aJCas);
      
     
    } catch (Exception ex) {
      System.err.println("Ontology Service Exception!");
      ex.printStackTrace();
    }
  }
  
  public JCas addSelectedService(OntologyServiceResponse.Result Result, String text, JCas aJCas){
    
    // Rank the returned concepts and add them to CAS
    int currRank = 0;
    for (Finding finding : Result.getFindings()) {
      Concept concept = new Concept(aJCas);
      concept.setName(finding.getConcept().getLabel());
      concept.addToIndexes();

      ConceptSearchResult result = new ConceptSearchResult(aJCas);
      result.setConcept(concept);
      result.setUri(finding.getConcept().getUri());
      result.setScore(finding.getScore());
      result.setText(finding.getConcept().getLabel());
      result.setRank(currRank++);
      result.setQueryString(text);
      result.addToIndexes();
    }
    return aJCas;
  }
}
