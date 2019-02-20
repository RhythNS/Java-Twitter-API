package everyst.analytics.smallHelpers.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONObject;

public class FileToJSON {

	/**
	 * Converts a File to a JSON Object
	 * 
	 * @param file - The file where the JSON object is
	 */
	public static JSONObject convert(File file) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		for (String s = br.readLine(); s != null; s = br.readLine()) {
			sb.append(s);
		}
		br.close();
		return new JSONObject(sb.toString());
	}

}
