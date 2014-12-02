package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Help to query the full text
 * @author Fei Xia <feixia@cs.cmu.edu>
 */

public class WebServiceHelper {
  private static final String PREFIX_DocFullText =  "http://metal.lti.cs.cmu.edu:30002/pmc/";
  
  /**
   * Read all text from the reader
   * @param rd the reader
   * @return the text string
   */
  private static String readAll(Reader rd) {
    StringBuilder sb = new StringBuilder();
    char[] buf = new char[4086];
    int numRead;
    
    try {
      while ((numRead = rd.read(buf)) != -1) {
        sb.append(buf, 0, numRead);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return sb.toString();
  }

  /**
   * Read json from the given url
   * @param url the given url
   * @return the json object
   */
  public static JsonObject readJsonFromUrl(String url) {
    InputStream is;
    JsonObject jsonObj = null;

    try {
      URL urlURL = new URL(url);
      URLConnection con = urlURL.openConnection();
      con.setConnectTimeout(1000);
      con.setReadTimeout(1000);
      is = con.getInputStream();

      //is = new URL(url).openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String jsonStr = readAll(rd);
      if (jsonStr.trim().length() == 0) {
        return null;
      }

      JsonElement jsonEle = new JsonParser().parse(jsonStr);
      jsonObj = jsonEle.getAsJsonObject();
    } catch (IOException e) {
      System.err.println("[Error]: Error or timeout!");
    } catch (Exception e) {
      System.err.println("[Error]: Other errors!");
    }
    //System.out.println(jsonObj);
    return jsonObj;
  }
  
  /**
   * Get json given a pubmed id
   * @param pmid the pubmed id
   * @return the json object
   */
  public static JsonObject getJsonFromPMID(String pmid) {
    String url = PREFIX_DocFullText + pmid;
    //System.out.println("Getting full text from: " + url);
    return readJsonFromUrl(url);
  }
  
  public static void main(String[] args) {
    JsonObject jsonObj = getJsonFromPMID("23193287");
    System.out.println(jsonObj.toString());
  }
}
