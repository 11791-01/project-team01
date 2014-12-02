package util;

import java.util.Comparator;

import edu.cmu.lti.deiis.project.assitance.RawSentence;
import edu.cmu.lti.oaqa.type.retrieval.Document;

/**
 * Used to do comparison
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */

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

  public static class SenSimComparator implements Comparator<RawSentence> {
    @Override
    public int compare(RawSentence lhs, RawSentence rhs) {
      if (lhs.getScore() < rhs.getScore()) {
        return 1;
      } else if (lhs.getScore() > rhs.getScore()) {
        return -1;
      } else {
        return 0;
      }
    }
  }
}
