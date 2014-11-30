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
 * @author Fei Xia <feixia@cs.cmu.edu>, Zexi Mao <zexim@cs.cmu.edu, Anurag Kumar <alnu@cs.cmu.edu>
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
      // String text = query.getWholeQueryWithoutOp();

      StringList queryTypes = query.getNamedEntityTypes();
      
     
      String querytype = queryTypes.getNthElement(0);
      
      // Use Mesh service
            
      mResultsPerPage = 6;
      OntologyServiceResponse.Result meshResult = service.findMeshEntitiesPaged(text, 0, mResultsPerPage);

      // Adding multiple sources here

      System.out.println(text);

      System.out.println("Mesh Results: " + meshResult.getFindings().size());
      //for (OntologyServiceResponse.Finding finding : meshResult.getFindings()) {
        // System.out.println(" > " + finding.getConcept().getLabel() + " "
        // + finding.getConcept().getUri()+"\t Score"+finding.getScore());
      //}

      
      int DOretsize = 20;
      int GOretsize = 20;
      //int JOretsize = 20;
      int UOretsize = 20;
      
      Double mthres = 0.1;
      Double DOthres = 0.1;
      Double GOthres = 0.1;
      //Double JOthres = 0.1;
      Double UOthres = 0.1;
      
      OntologyServiceResponse.Result diseaseOntologyResult = service.findDiseaseOntologyEntitiesPaged(text, 0,DOretsize);

      System.out.println("Disease ontology: " + diseaseOntologyResult.getFindings().size());
      //for (OntologyServiceResponse.Finding finding : diseaseOntologyResult.getFindings()) {
        // System.out.println(" > " + finding.getConcept().getLabel() + " "
        // + finding.getConcept().getUri()+"\t Score"+finding.getScore());
      //}

      OntologyServiceResponse.Result geneOntologyResult = service.findGeneOntologyEntitiesPaged(
              text, 0, GOretsize);
      System.out.println("Gene ontology: " + geneOntologyResult.getFindings().size());
      //for (OntologyServiceResponse.Finding finding : geneOntologyResult.getFindings()) {
        // System.out.println(" > " + finding.getConcept().getLabel() + " "
        // + finding.getConcept().getUri()+"\t Score"+finding.getScore());
      //}

      //OntologyServiceResponse.Result jochemResult = null;
      //try {
       // jochemResult = service.findJochemEntitiesPaged(text, 0,
        //        JOretsize);
        //System.out.println("Jochem: " + jochemResult.getFindings().size());
        //for (OntologyServiceResponse.Finding finding : jochemResult.getFindings()) {
          // System.out.println(" > " + finding.getConcept().getLabel() + " "
          // + finding.getConcept().getUri()+"\t Score"+finding.getScore());
        //}
      //} catch (Exception e) {
      //  e.printStackTrace();
      //}

      OntologyServiceResponse.Result uniprotResult = service.findUniprotEntitiesPaged(text, 0,
              UOretsize);
      System.out.println("UniProt: " + uniprotResult.getFindings().size());
      //for (OntologyServiceResponse.Finding finding : uniprotResult.getFindings()) {
        // System.out.println(" > " + finding.getConcept().getLabel() + " "
        // + finding.getConcept().getUri()+"\t Score"+finding.getScore());
      //}

      // Adding selective sources here
      // Double threshold = 0.1; //might decide to set different threshold for each service
      // List<Finding> meshPruneFinding;
      // meshPruneFinding = new ArrayList<Finding>();
      
      
      
      List<Finding> meshPrunedFinding = pruneFindings(meshResult, mthres);

      List<Finding> DOPrunedFinding = pruneFindings(diseaseOntologyResult, DOthres);

      List<Finding> GOPrunedFinding = pruneFindings(geneOntologyResult, GOthres);

      //List<Finding> JOPrunedFinding = pruneFindings(jochemResult, JOthres);

      List<Finding> UOPrunedFinding = pruneFindings(uniprotResult, UOthres);

      
      /*
       * take union of new objects defined --list of type weightedfinding
       * sort them
       * discuss first
       */
      //List<Finding> unionFinding = new ArrayList<Finding>();

      //unionFinding = CombineSources(meshPrunedFinding, unionFinding);
      //unionFinding = CombineSources(DOPrunedFinding, unionFinding);
      //unionFinding = CombineSources(GOPrunedFinding, unionFinding);
      //unionFinding = CombineSources(JOPrunedFinding, unionFinding);
      //unionFinding = CombineSources(UOPrunedFinding, unionFinding);

      
      //find weights to combine with
      
      List<Double> wts ;
      wts = new ArrayList<Double>();
      
      wts.add(1.0);
      wts.add(1.0);
      wts.add(1.0);
      //wts.add(1.0);
      wts.add(1.0);
      
      
      
      wts.add(multiplyByMean(meshPrunedFinding));
      wts.add(multiplyByMean(DOPrunedFinding));
      wts.add(multiplyByMean(GOPrunedFinding));
      //wts.add(multiplyByMean(JOPrunedFinding));
      wts.add(multiplyByMean(UOPrunedFinding));
      
      //Double wtmesh = multiplyByMean(meshPrunedFinding);
      //Double wtDO  = multiplyByMean(DOPrunedFinding);
      //Double wtGO  = multiplyByMean(GOPrunedFinding);
      //Double wtJO = multiplyByMean(JOPrunedFinding);
      //Double wtUO = multiplyByMean(UOPrunedFinding);
      
      
      
      List<Double> normwts=normalizeWtsSim(wts);
      
      //Double alpha = 0.5;
      //List<Double> normwts=normalizeWtsQuery(querytype,wts,alpha);
      
      
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
         + finding.getConcept().getUri()+"\t Score"+score);
      }

      aJCas = addSelectedServiceWtd(unionFinding, text, aJCas);

    } catch (Exception ex) {
      System.err.println("Ontology Service Exception!");
      ex.printStackTrace();
    }
  }

  private List<Finding> CombineSources(List<Finding> PrunedFinding, List<Finding> unionFinding) {

    for (Finding finding : PrunedFinding) {
      unionFinding.add(finding);
    }
    return unionFinding;
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
    
    if (Result == null) {
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
  
  private List<Double> normalizeWtsQuery(String querytype, List<Double> wts, double alpha){
    
    if(querytype == null ||querytype.isEmpty()){
      return wts;
    }
    List<Double> normwts = new ArrayList<Double>();
    if(querytype.equals("PROTEIN")){
      wts.set(3, wts.get(3)+(0.25*alpha));
      wts.set(4, wts.get(4)+(0.75*alpha)); 
    }
    else if(querytype.equals("GENE")){
      wts.set(3, wts.get(3)+(0.75*alpha)); 
      wts.set(4, wts.get(4)+(0.25*alpha)); 
    }
    normwts = normalizeWtsSim(wts);
    return normwts;
  }
private List<Double> normalizeWtsSim(List <Double> wts){
    
    List<Double> normwts;
    normwts = new ArrayList<Double>();
    Double sum = 0.0;
    for (Double wt : wts){
      sum+=wt;
    }
    for (int i = 0; i < wts.size(); i++){
      normwts.add(4*wts.get(i)/sum);
    }
    return normwts;
    
  }
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
  
   
  private JCas addSelectedService(List<Finding> unionFinding, String text, JCas aJCas) {

    // Rank the returned concepts and add them to CAS
    int currRank = 0;
    for (Finding finding : unionFinding) {

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
