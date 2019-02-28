package everyst.analytics.listner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;

public class KeyManager {

	private String consumerSecret, keyStorePassword, databaseName, databaseUser, databasePassword;

	public boolean readKeys(File file) {
		if (!file.exists()) {
			Logger.getInstance()
					.log("Key file has not been created yet. Please fill out the fill at :" + file.getAbsolutePath());
			createNewKeyFile(file);
			return false;
		} else if (file.isDirectory()) {
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
		consumerSecret = json.getString("consumer_secret");
		keyStorePassword = json.getString("keystore_password");
		databaseName = json.getString("database_name");
		databaseUser = json.getString("database_user");
		databasePassword = json.getString("database_password");

		if (consumerSecret == null || keyStorePassword == null || databaseName == null || databaseUser == null
				|| databasePassword == null) {
			Logger.getInstance().log("KeyManager: One or more keys could not be read from the JSON");
			return false;
		}

		return true;
	}

	private void createNewKeyFile(File file) {
		try {
			file.createNewFile();
			BufferedWriter write = new BufferedWriter(new FileWriter(file));
			write.write("{\n" + "\"consumer_secret\" : \"\",\n" + "\"keystore_password\" : \"\"\n"
					+ "\"database_name\" : \" \",\n" + "\"database_user\" : \" \",\n" + "\"database_password\" : \"\"\n"
					+ "}");
			write.flush();
			write.close();
		} catch (IOException e) {
			Logger.getInstance().handleError(e);
		}
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public String getDatabaseUser() {
		return databaseUser;
	}

}
