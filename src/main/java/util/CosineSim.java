package util;

import java.util.Hashtable;
import java.util.LinkedList;

//https://sites.google.com/site/nirajatweb/home/technical_and_coding_stuff/cosine_similarity 
public class CosineSim {
  public class values {
    int val1;

    int val2;

    values(int v1, int v2) {
      this.val1 = v1;
      this.val2 = v2;
    }

    public void Update_VAl(int v1, int v2) {
      this.val1 = v1;
      this.val2 = v2;
    }
  }// end of class values

  public double Cosine_Similarity_Score(String Text1, String Text2) {
    double sim_score = 0.0000000;
    // 1. Identify distinct words from both documents
    String[] word_seq_text1 = Text1.split(" ");
    String[] word_seq_text2 = Text2.split(" ");
    Hashtable<String, values> word_freq_vector = new Hashtable<String, values>();
    LinkedList<String> Distinct_words_text_1_2 = new LinkedList<String>();

    // prepare word frequency vector by using Text1
    for (int i = 0; i < word_seq_text1.length; i++) {
      String tmp_wd = word_seq_text1[i].trim();
      if (tmp_wd.length() > 0) {
        if (word_freq_vector.containsKey(tmp_wd)) {
          values vals1 = word_freq_vector.get(tmp_wd);
          int freq1 = vals1.val1 + 1;
          int freq2 = vals1.val2;
          vals1.Update_VAl(freq1, freq2);
          word_freq_vector.put(tmp_wd, vals1);
        } else {
          values vals1 = new values(1, 0);
          word_freq_vector.put(tmp_wd, vals1);
          Distinct_words_text_1_2.add(tmp_wd);
        }
      }
    }

    // prepare word frequency vector by using Text2
    for (int i = 0; i < word_seq_text2.length; i++) {
      String tmp_wd = word_seq_text2[i].trim();
      if (tmp_wd.length() > 0) {
        if (word_freq_vector.containsKey(tmp_wd)) {
          values vals1 = word_freq_vector.get(tmp_wd);
          int freq1 = vals1.val1;
          int freq2 = vals1.val2 + 1;
          vals1.Update_VAl(freq1, freq2);
          word_freq_vector.put(tmp_wd, vals1);
        } else {
          values vals1 = new values(0, 1);
          word_freq_vector.put(tmp_wd, vals1);
          Distinct_words_text_1_2.add(tmp_wd);
        }
      }
    }

    // calculate the cosine similarity score.
    double VectAB = 0.0000000;
    double VectA_Sq = 0.0000000;
    double VectB_Sq = 0.0000000;

    for (int i = 0; i < Distinct_words_text_1_2.size(); i++) {
      values vals12 = word_freq_vector.get(Distinct_words_text_1_2.get(i));

      double freq1 = (double) vals12.val1;
      double freq2 = (double) vals12.val2;
      System.out.println(Distinct_words_text_1_2.get(i) + "#" + freq1 + "#" + freq2);

      VectAB = VectAB + (freq1 * freq2);

      VectA_Sq = VectA_Sq + freq1 * freq1;
      VectB_Sq = VectB_Sq + freq2 * freq2;
    }
    System.out.println("VectAB " + VectAB + " VectA_Sq " + VectA_Sq + " VectB_Sq " + VectB_Sq);
    sim_score = ((VectAB) / (Math.sqrt(VectA_Sq) * Math.sqrt(VectB_Sq)));

    return (sim_score);
  }
}
