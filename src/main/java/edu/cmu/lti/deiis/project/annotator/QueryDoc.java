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
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.AtomicQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.Document;

/**
 * Query to get the document
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class QueryDoc extends JCasAnnotator_ImplBase {

  /*
   * The document uri prefix, used to construct the final document uri
   */
  private static final String DOCURI_PREFIX = "http://www.ncbi.nlm.nih.gov/pubmed/";

  /*
   * The GoPubMedService
   */
  private GoPubMedService service;

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

  /**
   * Get the documents and add them to JCas index
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

      PubMedSearchServiceResponse.Result pubmedResult = service.findPubMedCitations(text, 0);

      List<PubMedSearchServiceResponse.Document> docList = pubmedResult.getDocuments();
      for (int i = 0; i < docList.size(); ++i) {
        PubMedSearchServiceResponse.Document doc = docList.get(i);
        String docID = doc.getPmid();
        String uri = DOCURI_PREFIX + docID;

        Document document = new Document(aJCas);
        document.setUri(uri);
        document.setRank(i);
        document.setTitle(doc.getTitle());
        document.setDocId(docID);
        document.addToIndexes();
      }

    } catch (Exception ex) {

    }
  }
}
