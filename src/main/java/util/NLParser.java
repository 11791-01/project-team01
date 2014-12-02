package util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.uima.resource.ResourceInitializationException;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
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

/**
 * NLParser class, used to detect positive or negative sentence
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */

public class NLParser {

  /**
   * The standford nlp pipeline
   */
  private StanfordCoreNLP pipeline;

  /**
   * The negative word set
   */
  private Set<String> negWordSet;

  /**
   * Constructor. Construct the Stanford NLP pipeline.
   * 
   * @throws ResourceInitializationException
   */
  public NLParser() throws ResourceInitializationException {
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, parse");
    pipeline = new StanfordCoreNLP(props);

    negWordSet = new HashSet<String>();
    String content = FileOp.getFileAsStream("models/negwords.txt", NLParser.class);
    String[] lines = content.split("\n");
    for (String line : lines) {
      negWordSet.add(line);
    }
  }

  /**
   * Do the parse and return positive or negative
   * @param text the text
   * @return true if positive, false otherwise
   */
  public boolean doParse(String text) {
    Annotation document = new Annotation(text);
    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);

    boolean flag = true;

    for (CoreMap sentence : sentences) {
      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
      Set<SemanticGraphEdge> edges = dependencies.getEdgeSet();
      for (SemanticGraphEdge edge : edges) {
        GrammaticalRelation rel = edge.getRelation();
        String name = rel.getShortName();
        if (name.equals("neg")) {
          flag = false;
        }
      }

      List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
      for (CoreLabel token : tokens) {
        String lemma = token.get(LemmaAnnotation.class);
        if (negWordSet.contains(lemma)) {
          flag = false;
        }
      }
    }

    return flag;
  }

  public static void main(String[] args) throws Exception {
    NLParser myParser = new NLParser();
    myParser.doParse("He denies this.");
  }

}
