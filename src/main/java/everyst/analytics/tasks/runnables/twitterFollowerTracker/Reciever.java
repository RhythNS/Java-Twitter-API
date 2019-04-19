package everyst.analytics.tasks.runnables.twitterFollowerTracker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.utility.JSONUtil;
import everyst.analytics.listner.utility.LinuxProcess;
import everyst.analytics.listner.utility.TimeUtility;
import everyst.analytics.listner.dataManagement.Logger;

public class Reciever implements Runnable {

	private DataManagement[] dataManagement;
	private int count, requests;
	private final String TAB = "\t";
	private String[] accounts;

	public Reciever(int count, File root, File accountsFile) throws NumberFormatException, IOException, JSONException {
		this.count = count;
		this.accounts = readAccounts(accountsFile);
		dataManagement = new DataManagement[accounts.length];
		for (int i = 0; i < accounts.length; i++) {
			dataManagement[i] = new DataManagement(this, root, accounts[i]);
		}
	}

	private String[] readAccounts(File accounts) throws NumberFormatException, IOException, JSONException {
		JSONObject json = JSONUtil.convert(accounts);
		JSONArray accountArray = json.getJSONArray("accounts");
		String[] retArr = new String[accountArray.length()];

		for (int i = 0; i < accountArray.length(); i++) {
			retArr[i] = (String) accountArray.get(i);
		}

		return retArr;
	}

	/**
	 * Gets all ids from eddies account
	 * 
	 * @return success
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean recieve(String accountName, DataManagement dataManagement) throws IOException, InterruptedException {
		ArrayList<Long> ids = new ArrayList<>();
		String cursor = "";
		while (true) {
			String result = LinuxProcess.execute("twurl", "/1.1/followers/ids.json?screen_name=" + accountName
					+ "&count=" + count + (cursor.equals("") ? "" : "&cursor=" + cursor));

			// debug to see how big length of the file is or if an error occured print it
			if (result.length() < 100)
				Logger.getInstance().log(result);

			try {
				JSONObject json = new JSONObject(result);
				JSONArray jsonIDS = json.getJSONArray("ids");

				// cast everything. Sometime it is an integer sometimes long /shrug
				for (Object obj : jsonIDS) {
					if (obj instanceof Long)
						ids.add((Long) obj);
					else
						ids.add(new Long(((Integer) obj).toString()));
				}

				cursor = json.getString("next_cursor_str");
			} catch (JSONException e) {
				Logger.getInstance().handleError(e);
				return false;
			}

			madeRequest();

			if (cursor.equals("0"))
				break;
		}

		// now we have the ids so send them to the datamanagement
		dataManagement.recieveIds(ids);

		return true;
	}

	public String convertTagsToInformation(ArrayList<Long> tags) throws InterruptedException {
		if (tags.isEmpty())
			return "";

		StringBuilder informationBuilder = new StringBuilder();
		String time = TimeUtility.getTime();

		int timesToIterate = Math.max(tags.size() / 100, 1);

		for (int i = 0; i < timesToIterate; i++) {
			StringBuilder requestBuilder = new StringBuilder();
			requestBuilder.append(tags.get(i * 100));

			// make the request for the twitter api
			for (int j = 1; j < 100 && i * 100 + j < tags.size(); j++) {
				requestBuilder.append("," + tags.get(i * 100 + j));
			}

			String result = "";
			String request = requestBuilder.toString();
			try {
				// ask twitter for the information of the users
				result = LinuxProcess.execute("twurl", "/1.1/users/lookup.json?user_id=" + request);

				JSONArray json = new JSONArray(result);
				for (int j = 0; j < json.length(); j++) { // For every element in the JSON
					JSONObject user = json.getJSONObject(j);

					// get all relevant information from the user
					String id = user.getString("id_str");
					String screenName = user.getString("screen_name");
					String location = user.getString("location");
					int friends = user.getInt("friends_count");
					int followers = user.getInt("followers_count");

					// Append it ugly to the information builder
					informationBuilder.append(time).append(TAB).append(id).append(TAB).append(screenName).append(TAB)
							.append(friends).append(TAB).append(followers).append(TAB).append(location)
							.append(System.lineSeparator());
				} // end iterating the JSON

			} catch (IOException e) {
				Logger.getInstance().handleError(e);
				e.printStackTrace();
				return null;
			} catch (JSONException j) { // if an unexpected JSON exception
				if (!result.replaceAll("\\s", "").contains("\"code\":17")) {
					Logger.getInstance().handleError(j);
					Logger.getInstance().log("--- Sent ---\n" + request + "--- Recieved ---\n" + result);
					return null;
				}
			}

		} // end iteration

		return informationBuilder.toString();
	}

	private void madeRequest() throws InterruptedException {
		if (requests++ > 13) {
			Thread.sleep(900000);
			requests = 0;
		}
	}

	@Override
	public void run() {
		requests = 0;
		for (int i = 0; i < accounts.length; i++) {
			boolean failed = true;
			for (int tries = 0; tries < 3; tries++) {
				try {
					if (recieve(accounts[i], dataManagement[i])) {
						failed = false;
						break;
					} else {
						requests = 15;
						madeRequest();
					}
				} catch (IOException e) {
					Logger.getInstance().handleError(e);
				} catch (InterruptedException e) {
					Logger.getInstance().log("Followerlist not fully updated!");
				}
			}
			if (failed) {
				Logger.getInstance().log("Could not get account info for " + accounts[i] + "! Exiting Process!");
				return;
			}
		}
	}

	public static void main(String[] args) throws NumberFormatException, JSONException, IOException {
		new Reciever(3500, new File("Testing"), new File("accounts")).run();
	}

}
