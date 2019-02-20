package everyst.analytics.listner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;

public class KeyManager {

	private String consumerKey, consumerSecret, token, tokenSecret, keyStorePassword;

	public boolean readKeys(File file) {
		if (!file.exists() || file.isDirectory()) {
			Logger.getInstance().log(file.getAbsolutePath() + " is either a directory or does not exist");
			return false;
		}

		StringBuilder sb = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			for (String s = br.readLine(); s != null; s = br.readLine()) {
				sb.append(s);
			}
			br.close();

		} catch (IOException e) {
			Logger.getInstance().handleError(e);
			return false;
		}

		JSONObject json = new JSONObject(sb.toString());
		consumerKey = json.getString("consumer_key");
		consumerSecret = json.getString("consumer_secret");
		token = json.getString("token");
		tokenSecret = json.getString("token_secret");
		keyStorePassword = json.getString("keystore_password");

		if (consumerKey == null || consumerSecret == null || token == null || tokenSecret == null
				|| keyStorePassword == null) {
			Logger.getInstance().log("KeyManager: One or more keys could not be read from the JSON");
			return false;
		}

		return true;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getToken() {
		return token;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

}
