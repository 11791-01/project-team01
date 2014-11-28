package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.uima.resource.ResourceInitializationException;

/**
 * Perform Basic File Operation, read or write.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 *
 */
public class FileOp {
  /**
   * Open a the file in filepath and write the content into it.
   * 
   * @param filePath
   *          the file path
   * @param content
   *          the content
   */
  public static void writeToFile(String filePath, String content) {
    try {
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),
              "UTF-8"));
      bw.write(content);
      bw.close();
    } catch (IOException ex) {
      System.err.println("Error: cannot open file: " + filePath);
      System.exit(1);
    }
  }

  /**
   * Read content from a file
   * 
   * @param filename
   *          the file path
   * @return the content of the file
   */
  public static String readFromFile(String filename) {
    String content = null;
    StringBuilder sb = new StringBuilder();
    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));

      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        // sb.append(System.lineSeparator());
        sb.append("\n");
        line = br.readLine();
      }
      br.close();

    } catch (IOException fexp) {
      System.out.println("Error: error in open file: " + filename);
      System.exit(1);
    }

    content = sb.toString();
    return content;
  }

  /**
   * Read file through stream for the runtime class. Note: the file path should be WITHOUT the
   * forward slash
   * 
   * @param filePath
   *          the file path, without forward slash
   * @param runtimeClass
   *          the runtime class
   * @return the string of the file
   * @throws ResourceInitializationException
   */
  public static String getFileAsStream(String filePath, Class<?> runtimeClass)
          throws ResourceInitializationException {
    StringBuilder sb = new StringBuilder();
    try {
      InputStream is = runtimeClass.getClassLoader().getResourceAsStream(filePath);

      BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));

      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      br.close();
    } catch (Exception ex) {
      System.out.println("[Error]: Look Below.");
      ex.printStackTrace();
      System.out.println("[Error]: Look Above.");
      throw new ResourceInitializationException();
    }

    String content = sb.toString();
    return content;
  }
}
