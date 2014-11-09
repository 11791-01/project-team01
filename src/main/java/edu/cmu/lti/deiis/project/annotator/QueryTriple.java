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
import edu.cmu.lti.oaqa.type.retrieval.AtomicQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;

public class QueryTriple extends JCasAnnotator_ImplBase {

  GoPubMedService service;

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
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    FSIterator<TOP> queryIter = aJCas.getJFSIndexRepository().getAllIndexedFS(
            ComplexQueryConcept.type);

    try {
      ComplexQueryConcept query = (ComplexQueryConcept) queryIter.next();

      List<AtomicQueryConcept> queryList = (ArrayList<AtomicQueryConcept>) Utils
              .fromFSListToCollection(query.getOperatorArgs(), AtomicQueryConcept.class);
      String text = queryList.get(0).getText();

      System.out.println("Query Triple!");
      LinkedLifeDataServiceResponse.Result linkedLifeDataResult = service
              .findLinkedLifeDataEntitiesPaged(text, 0);
      System.out.println("LinkedLifeData: " + linkedLifeDataResult.getEntities().size());
      for (LinkedLifeDataServiceResponse.Entity entity : linkedLifeDataResult.getEntities()) {
        System.out.println(" > " + entity.getEntity());
        for (LinkedLifeDataServiceResponse.Relation relation : entity.getRelations()) {
          System.out.println("   - labels: " + relation.getLabels());
          System.out.println("   - pred: " + relation.getPred());
          System.out.println("   - sub: " + relation.getSubj());
          System.out.println("   - obj: " + relation.getObj());
        }
      }

    } catch (Exception ex) {

    }
  }
}
