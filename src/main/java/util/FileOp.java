package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
	 *            the file path
	 * @param content
	 *            the content
	 */
	public static void writeToFile(String filePath, String content) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					filePath)));
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
	 * @param filename the file path
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
				//sb.append(System.lineSeparator());
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
}
