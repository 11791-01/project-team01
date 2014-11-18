package json.gson;

import java.util.List;

public class OutputQuestion extends Question {

  /**
   * Constructor for Milestone 1 (concepts, documents, triples only)
   * @param id
   * @param body
   * @param documents
   * @param concepts
   * @param triples
   */
  public OutputQuestion(String id, String body, List<String> documents,
          List<String> concepts, List<Triple> triples) {
    super(id, body, null, documents, null, concepts, triples);
  }
  
  /**
   * Constructor for Milestone 2 (concepts, documents, triples, snippets only)
   * @param id
   * @param body
   * @param documents
   * @param concepts
   * @param triples
   * @param snippets
   */
  public OutputQuestion(String id, String body, List<String> documents,
          List<String> concepts, List<Triple> triples, List<Snippet> snippets) {
    super(id, body, null, documents, snippets, concepts, triples);
  }
  
  public OutputQuestion(String id, String body, QuestionType type, List<String> documents,
          List<Snippet> snippets, List<String> concepts, List<Triple> triples, String idealAnswer) {
    super(id, body, type, documents, snippets, concepts, triples);
    
  }

}