package everyst.analytics.smallHelpers.twitterFollowerTracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.utility.LinuxProcess;
import everyst.analytics.listner.utility.TimeUtility;
import everyst.analytics.listner.dataManagement.Logger;

public class Reciever {

	private DataManagement dataManagement;
	private int count;
	private final String TAB = "\t", accountName;

	public Reciever(int count, String accountName) {
		this.count = count;
		this.accountName = accountName;
		dataManagement = new DataManagement(this);
	}

	/**
	 * Gets all ids from eddies account
	 * 
	 * @return success
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean recieve() throws IOException, InterruptedException {
		ArrayList<Long> ids = new ArrayList<>();
		String cursor = "";
		do {

			String result = LinuxProcess.execute("twurl", "/1.1/followers/ids.json?screen_name=" + accountName
					+ "&count=" + count + (cursor.equals("") ? "" : "&cursor=" + cursor));

			// debug to see how big length of the file is or if an error occured print it
			if (result.length() < 2000)
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
		} while (!cursor.equals("0")); // do everything until every id has been added

		// now we have the ids so send them to the datamanagement
		dataManagement.recieveIds(ids);

		return true;
	}

	public String convertTagsToInformation(ArrayList<Long> tags) {
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

			} catch (IOException | InterruptedException e) {
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

	public static void main(String[] args) {
		System.out.print("Count (3500 seemed to work): ");
		Scanner scan = new Scanner(System.in);
		int count = scan.nextInt();
		System.out.println("For which account?: ");
		String accountName = scan.nextLine();
		scan.close();

		Logger.getInstance().log("Now Running with count: " + count);

		Reciever rec = new Reciever(count, accountName);

		while (true) {
			try {
				if (!rec.recieve())
					Logger.getInstance().log("Failed!");
				Thread.sleep(900000);
			} catch (IOException | InterruptedException e) {
				Logger.getInstance().handleError(e);
			}
		}
	}

}
