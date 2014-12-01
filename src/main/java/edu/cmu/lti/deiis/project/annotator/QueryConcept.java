package edu.cmu.lti.deiis.project.annotator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.resource.ResourceInitializationException;

import util.WeightedFinding;
import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;

/**
 * Query to get the concept.
 * 
 * @author Anurag Kumar <alnu@cs.cmu.edu, Fei Xia <feixia@cs.cmu.edu>, Zexi Mao <zexim@cs.cmu.edu>
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
      ComplexQueryConcept query = (ComplexQueryConcept) queryIter.next();

      // Get the query text
      String text = query.getWholeQueryWithOp();
      

      StringList queryTypes = query.getNamedEntityTypes();
      
     
      String querytype = queryTypes.getNthElement(0);
      
      // Use Mesh service
            
      mResultsPerPage = 6;
      OntologyServiceResponse.Result meshResult = service.findMeshEntitiesPaged(text, 0, mResultsPerPage);

      // Adding multiple sources here
  
      int DOretsize = 6;
      int GOretsize = 6;
      //int JOretsize = 20;
      int UOretsize = 6;
      
      Double mthres = 0.1;
      Double DOthres = 0.1;
      Double GOthres = 0.1;
      //Double JOthres = 0.1;
      Double UOthres = 0.1;
      
      OntologyServiceResponse.Result diseaseOntologyResult = null;
      diseaseOntologyResult = service.findDiseaseOntologyEntitiesPaged(text, 0,DOretsize);

      System.out.println("Disease ontology: " + diseaseOntologyResult.getFindings().size());

      OntologyServiceResponse.Result geneOntologyResult = null;
      geneOntologyResult = service.findGeneOntologyEntitiesPaged(
              text, 0, GOretsize);
      System.out.println("Gene ontology: " + geneOntologyResult.getFindings().size());

      //OntologyServiceResponse.Result jochemResult = null;
      //try {
       // jochemResult = service.findJochemEntitiesPaged(text, 0,
        //        JOretsize);
        //System.out.println("Jochem: " + jochemResult.getFindings().size());
      //} catch (Exception e) {
      //  e.printStackTrace();
      //}
      OntologyServiceResponse.Result uniprotResult = null;
      uniprotResult = service.findUniprotEntitiesPaged(text, 0,
              UOretsize);
      System.out.println("UniProt: " + uniprotResult.getFindings().size());

            
      List<Finding> meshPrunedFinding = pruneFindings(meshResult, mthres);

      List<Finding> DOPrunedFinding = pruneFindings(diseaseOntologyResult, DOthres);

      List<Finding> GOPrunedFinding = pruneFindings(geneOntologyResult, GOthres);

      //List<Finding> JOPrunedFinding = pruneFindings(jochemResult, JOthres);

      List<Finding> UOPrunedFinding = pruneFindings(uniprotResult, UOthres);

      
      //find weights to combine with
      
      List<Double> wts ;
      wts = new ArrayList<Double>();

      wts.add(1.0);
      wts.add(1.0);
      wts.add(1.0);
      ////wts.add(1.0);
      wts.add(1.0);
      
      //wts.add(multiplyByMean(meshPrunedFinding));

      //wts.add(multiplyByMean(DOPrunedFinding));
      //wts.add(multiplyByMean(GOPrunedFinding));
      ////wts.add(multiplyByMean(JOPrunedFinding));
      //wts.add(multiplyByMean(UOPrunedFinding));
      
      
      System.out.println("Weights" + wts);
      List<Double> normwts=normalizeWtsSim(wts);

      System.out.println("Normal Weights" + normwts);
      Double alpha = 1.0;


      //List<Double> normwts=normalizeWtsQuery(querytype,wts,alpha);

      //normwts=normalizeWtsQuery(querytype,normwts,alpha);
      System.out.println("Query based normal Weights for query " + querytype + "is"+ normwts);

      normwts=normalizeWtsQuery(querytype,wts,alpha);
      
      List<WeightedFinding> unionFinding = new ArrayList<WeightedFinding>();
      
      
      unionFinding = CombineSourcesWeighted(meshPrunedFinding, unionFinding,normwts.get(0));
      unionFinding = CombineSourcesWeighted(DOPrunedFinding, unionFinding,normwts.get(1));
      unionFinding = CombineSourcesWeighted(GOPrunedFinding, unionFinding,normwts.get(2));
      //unionFinding = CombineSourcesWeighted(JOPrunedFinding, unionFinding,wtJO);
      unionFinding = CombineSourcesWeighted(UOPrunedFinding, unionFinding,normwts.get(3));
      
      
      
      // sort union finding
      Collections.sort(unionFinding,
              (s1, s2) -> ((Double) s2.getNewSco()).compareTo((Double) s1.getNewSco()));

      // print the union
      System.out.println("Printing the Union"+unionFinding.size());
      for (WeightedFinding wtfinding : unionFinding) {

         Double score = wtfinding.getNewSco();
         Finding finding = wtfinding.getfndg();
         System.out.println(" > " + finding.getConcept().getLabel() + " "
         + finding.getConcept().getUri()+"\t ScoreOrg"+finding.getScore()+"\t ScoreFin"+score);
      }

      aJCas = addSelectedServiceWtd(unionFinding, text, aJCas);

    } catch (Exception ex) {
      System.err.println("Ontology Service Exception!");
      ex.printStackTrace();
    }
  }

 
  private List<WeightedFinding> CombineSourcesWeighted(List<Finding> PrunedFinding, List<WeightedFinding> unionFinding, Double wt) {

    for (Finding finding : PrunedFinding) {
      WeightedFinding wtfnd = new WeightedFinding(finding, finding.getScore()*wt);
      unionFinding.add(wtfnd);
    }
    return unionFinding;
  }
  
  
  private List<Finding> pruneFindings(OntologyServiceResponse.Result Result, Double threshold) {

    List<Finding> prunedFinding;
    prunedFinding = new ArrayList<Finding>();
    
    if (Result == null) {
      return prunedFinding;
    }
    
    for (Finding finding : Result.getFindings()) {

      if (finding.getScore() >= threshold) {
        prunedFinding.add(finding);
      }

    }

    return prunedFinding;

  }

  private double multiplyByMean(List<Finding> Result){
    
    if (Result.size() == 0 || Result == null) {
      return 0.0;
    }
    double allscores = 0.0;
    int count = 0;
    for (Finding finding : Result){
      allscores+=finding.getScore();
      count+=1;
    }
    return allscores/count;
  }
  /**
   * Re-weighing based on the query
   * @param querytype - if query id gene or protein
   * @param wts - initial weights
   * @param alpha - amount to add add in weights
   * @return - query based weights
   */
  private List<Double> normalizeWtsQuery(String querytype, List<Double> wts, double alpha){
    
    if(querytype == null ||querytype.isEmpty()){
      return wts;
    }
    List<Double> normwts = new ArrayList<Double>();
    if(querytype.equals("PROTEIN")){
      wts.set(2, wts.get(2)+(0.25*alpha));
      wts.set(3, wts.get(3)+(0.75*alpha)); 
    }
    else if(querytype.equals("GENE")){
      wts.set(2, wts.get(2)+(0.75*alpha)); 
      wts.set(3, wts.get(3)+(0.25*alpha)); 
    }
    normwts = normalizeWtsSim(wts);
    return normwts;
  }
  /**
   * Simple normalization of weights for different sources
   * @param wts - initial weights
   * @return normalized weights
   */
private List<Double> normalizeWtsSim(List <Double> wts){
    
    List<Double> normwts;
    normwts = new ArrayList<Double>();
    Double sum = 0.0;
    for (Double wt : wts){
      sum+=wt;
    }
    for (int i = 0; i < wts.size(); i++){
      normwts.add((4*wts.get(i))/sum);
    }
    return normwts;
    
  }

/**
 * 
 * @param unionFinding - union of all concepts
 * @param text - query text
 * @param aJCas - to add to jcas
 * @return items added to ajCas
 */
  private JCas addSelectedServiceWtd(List<WeightedFinding> unionFinding, String text, JCas aJCas) {

    // Rank the returned concepts and add them to CAS
    int currRank = 0;
    for (WeightedFinding wtfinding : unionFinding) {

      Finding finding = wtfinding.getfndg();
      Double newsco = wtfinding.getNewSco();

      Concept concept = new Concept(aJCas);
      concept.setName(finding.getConcept().getLabel());
      concept.addToIndexes();

      ConceptSearchResult result = new ConceptSearchResult(aJCas);
      result.setConcept(concept);
      result.setUri(finding.getConcept().getUri());
      result.setScore(newsco);
      result.setText(finding.getConcept().getLabel());
      result.setRank(currRank++);
      result.setQueryString(text);
      result.addToIndexes();
    }
    return aJCas;
  }

}
