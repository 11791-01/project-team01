package util;

import java.util.Comparator;

import edu.cmu.lti.oaqa.type.retrieval.Document;

public class MyComp {
  public static class DocSimComparator implements Comparator<Document> {
    @Override
    public int compare(Document lhs, Document rhs) {
      if (lhs.getScore() < rhs.getScore()) {
        return 1;
      } else if (lhs.getScore() > rhs.getScore()) {
        return -1;
      } else {
        return lhs.getRank() - rhs.getRank();
      }
    }
  }

}
