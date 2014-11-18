package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>
 */

public class WebServiceHelper {
  private static final String PREFIX_DocFullText =  "http://metal.lti.cs.cmu.edu:30002/pmc/";
  
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

  public static JsonObject readJsonFromUrl(String url) {
    InputStream is;
    JsonObject jsonObj = null;

    try {
      is = new URL(url).openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String jsonStr = readAll(rd);
      if (jsonStr.trim().length() == 0) {
        return null;
      }

      JsonElement jsonEle = new JsonParser().parse(jsonStr);
      jsonObj = jsonEle.getAsJsonObject();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    } 

    return jsonObj;
  }
  
  public static JsonObject getJsonFromPMID(String pmid) {
    String url = PREFIX_DocFullText + pmid;
    return readJsonFromUrl(url);
  }
  
  public static void main(String[] args) {
    JsonObject jsonObj = getJsonFromPMID("23193287");
    System.out.println(jsonObj.toString());
  }
}
