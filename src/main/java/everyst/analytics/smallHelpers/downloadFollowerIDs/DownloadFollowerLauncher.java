package everyst.analytics.smallHelpers.downloadFollowerIDs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.utility.LinuxProcess;

public class DownloadFollowerLauncher implements Runnable {

	private String[] names;
	private File root;

	public DownloadFollowerLauncher(File root, String...names) {
		this.names = names;
		this.root = root;
		if (!root.exists())
			root.mkdirs();
		if (!root.isDirectory())
			throw new IllegalStateException("Root in DownloadFollowerLauncher is not a directory!");
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < names.length; i++) {
				File jsonFile = new File(root, names[i] + "json"), idFile = new File(root, names[i] + "ids");
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
		} catch (IOException | InterruptedException e) {
			Logger.getInstance().log("Could not download the follower lists!");
			Logger.getInstance().handleError(e);
		}
	}
	
	public static void main(String[] args) {
		new DownloadFollowerLauncher(new File("testing"), "HMagKohaku").run();
	}

}
