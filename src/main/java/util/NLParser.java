package util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.uima.resource.ResourceInitializationException;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.TypesafeMap.Key;

public class NLParser {

  private StanfordCoreNLP pipeline;

  public NLParser() throws ResourceInitializationException {
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, parse");
    pipeline = new StanfordCoreNLP(props);
  }
  
  public boolean doParse(String text) {
    Annotation document = new Annotation(text);
    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
    
    for (CoreMap sentence : sentences) {
      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
      Set<SemanticGraphEdge> edges = dependencies.getEdgeSet();
      for (SemanticGraphEdge edge : edges) {
        GrammaticalRelation rel = edge.getRelation();
        String name = rel.getShortName();
        if (name.equals("neg")) {
          return false;
        }
      }
    }

    return true;
  }
  
  public static void main(String[] args) throws Exception {
    NLParser myParser = new NLParser();
    myParser.doParse("I am not happy!");
  }

}
