package everyst.analytics.listner.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {

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

	public static JSONArray getArray(String key, JSONObject json) {
		try {
			return json.getJSONArray(key);
		} catch (JSONException e) {
			return null;
		}
	}

}
