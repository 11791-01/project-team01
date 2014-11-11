package edu.cmu.lti.deiis.project.annotator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.resource.ResourceInitializationException;

import util.Utils;
import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.AtomicQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;

/**
 * Query to get the triples
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class QueryTriple extends JCasAnnotator_ImplBase {

  /**
   * The GoPubMedService
   */
  GoPubMedService service;

  /**
   * The number of results in each retrieved pages
   */
  private int mResultsPerPage;

  /**
   * Perform initialization logic. Initialize the service.
   * 
   * @param aContext
   *          the UimaContext object
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    try {
      service = new GoPubMedService("project.properties");
    } catch (Exception ex) {
      throw new ResourceInitializationException();
    }

    mResultsPerPage = (int) getContext().getConfigParameterValue("ResultsPerPage");
  }

  /**
   * Get the triples and add them to the JCas index
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    FSIterator<TOP> queryIter = aJCas.getJFSIndexRepository().getAllIndexedFS(
            ComplexQueryConcept.type);

    try {
      ComplexQueryConcept query = (ComplexQueryConcept) queryIter.next();

      List<AtomicQueryConcept> queryList = (ArrayList<AtomicQueryConcept>) Utils
              .fromFSListToCollection(query.getOperatorArgs(), AtomicQueryConcept.class);
      String text = queryList.get(0).getText();

      LinkedLifeDataServiceResponse.Result linkedLifeDataResult = service
              .findLinkedLifeDataEntitiesPaged(text, 0, mResultsPerPage);
      List<LinkedLifeDataServiceResponse.Entity> entities = linkedLifeDataResult.getEntities();

      for (int i = 0; i < entities.size(); ++i) {
        LinkedLifeDataServiceResponse.Entity entity = entities.get(i);
        LinkedLifeDataServiceResponse.Relation relation = entity.getRelations().get(0);

        Triple triple = new Triple(aJCas);
        triple.setSubject(relation.getSubj());
        triple.setPredicate(relation.getPred());
        triple.setObject(relation.getObj());
        triple.addToIndexes();

        TripleSearchResult tripleSR = new TripleSearchResult(aJCas);
        tripleSR.setRank(i);
        tripleSR.setTriple(triple);
        tripleSR.addToIndexes();
      }

    } catch (Exception ex) {

    }
  }
}
