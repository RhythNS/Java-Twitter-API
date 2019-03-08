package everyst.analytics.smallHelpers.downloadFollowerIDs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.utility.LinuxProcess;

public class DownloadFollowerLauncher {

	public static void main(String[] args) throws InterruptedException, IOException {

		Scanner scan = new Scanner(System.in);
		System.out.println(
				"Type in the names you want to pull the Follower IDs from (Seperated by , and the names all lowercase!):");
		String[] names = scan.nextLine().split(",");

		System.out.println("Getting the followers for:");
		for (int i = 0; i < names.length; i++) {
			System.out.println(names[i]);
		}
		System.out.println("Enter anything to continue ");
		scan.nextLine();

		scan.close();
		for (int i = 0; i < names.length; i++) {
			File jsonFile = new File(names[i] + "json"), idFile = new File(names[i] + "ids");
			if (!jsonFile.exists())
				jsonFile.createNewFile();
			if (!idFile.exists())
				idFile.createNewFile();

			BufferedWriter jsonWriter = new BufferedWriter(new FileWriter(jsonFile)),
					idWriter = new BufferedWriter(new FileWriter(idFile));
			String cursor = "";
			do {
				String result = LinuxProcess.execute("twurl", "/1.1/followers/ids.json?screen_name=" + names[i]
						+ "&count=" + 3500 + (cursor.equals("") ? "" : "&cursor=" + cursor));

				// debug to see how big length of the file is or if an error occured print it
				if (result.length() < 2000)
					Logger.getInstance().log(result);

				try {
					JSONObject json = new JSONObject(result);
					JSONArray jsonIDS = json.getJSONArray("ids");

					// cast everything. Sometime it is an integer sometimes long /shrug
					for (Object obj : jsonIDS) {
						if (obj instanceof Long)
							idWriter.write(((Long) obj).toString());
						else
							idWriter.write(((Integer) obj).toString());
						idWriter.write("\n");
					}

					cursor = json.getString("next_cursor_str");
				} catch (JSONException e) {
					Logger.getInstance().handleError(e);
					jsonWriter.write(result);
				}

				Thread.sleep(90 * 1000); // 90 sec wait
				
			} while (!cursor.equals("0")); // do everything until every id has been added

			try {
				jsonWriter.close();
				idWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
