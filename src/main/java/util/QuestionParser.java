/**
 * 
 */
package util;

import java.util.List;
import java.util.Properties;

import org.apache.uima.resource.ResourceInitializationException;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * A parser for parsing questions.
 * 
 * @author Zexi Mao <zexim@cs.cmu.edu>
 *
 */
public class QuestionParser {

  private StanfordCoreNLP pipeline;

  /**
   * Initialize a parser pipeline.
   * 
   * @throws ResourceInitializationException
   */
  public QuestionParser() throws ResourceInitializationException {
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
    pipeline = new StanfordCoreNLP(props);
  }

  /**
   * Get the root of a question sentence, for yes/no question, the root is most probably a verb
   * specifying the relationship of the entities.
   * 
   * @param text The question string.
   */
  public String getRoot(String text) {
    Annotation document = new Annotation(text);
    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
    CoreMap sentence = sentences.get(0);
    SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
    IndexedWord root = dependencies.getFirstRoot();
    
    return root.word();
  }
  
}
