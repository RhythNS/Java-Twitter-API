package everyst.analytics.tasks.runnables;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.utility.JSONUtil;
import everyst.analytics.listner.utility.LinuxProcess;
import everyst.analytics.mysql.MySQLConnection;
import everyst.analytics.mysql.MySQLDate;

public class DailyFollower implements Runnable {

	private String database, user, password;
	private ArrayList<Long> ids;
	private File file;

	public DailyFollower(File file) {
		this.file = file;
	}

	@Override
	public void run() {
		try {
			readFile(file);
			String json = recieve(ids);
			Map<Long, Integer> map = parseJSON(json);
			addToDatabase(map);
		} catch (NumberFormatException | IOException | InterruptedException e) {
			Logger.getInstance().handleError(e);
		}
	}

	public void readFile(File file) throws NumberFormatException, IOException {
		JSONObject json = JSONUtil.convert(file);
		database = json.getString("database");
		user = json.getString("user");
		password = json.getString("password");

		JSONArray array = json.getJSONArray("ids");
		ids = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			ids.add(Long.parseLong(array.getString(i)));
		}
	}

	public String recieve(ArrayList<Long> ids) throws IOException, InterruptedException {
		StringBuilder command = new StringBuilder();
		command.append("/1.1/users/lookup.json?user_id=").append(ids.get(0));
		for (int i = 1; i < ids.size(); i++) {
			command.append(",").append(ids.get(i));
		}
		return LinuxProcess.execute("twurl", command.toString());
	}

	public Map<Long, Integer> parseJSON(String rawJSON) {
		Map<Long, Integer> map = new TreeMap<>();
		JSONArray json = new JSONArray(rawJSON);
		for (int i = 0; i < json.length(); i++) {
			JSONObject user = (JSONObject) json.get(i);
			int followerCount = user.getInt("followers_count");
			long id = Long.parseLong(user.getString("id_str"));
			map.put(id, followerCount);
		}
		return map;
	}

	public void addToDatabase(Map<Long, Integer> map) {
		MySQLConnection conn;
		// Connect to the database
		try {
			conn = new MySQLConnection(database, user, password);
		} catch (SQLException e1) {
			Logger.getInstance().log("Could not connect to databasse!");
			Logger.getInstance().handleError(e1);
			return;
		}

		// Save the current date so we only generate it once
		String currentDate = MySQLDate.getCurrentTime();

		// iterate through the map
		for (Entry<Long, Integer> entry : map.entrySet()) {
			// INSERT INTO FollowerCount VALUES ('date', id, count);
			StringBuilder sb = new StringBuilder("INSERT INTO Follower_Count VALUES ('").append(currentDate)
					.append("', ").append(entry.getKey()).append(", ").append(entry.getValue()).append(");");

			// Finally execute it to the database
			try {
				conn.execute(sb.toString());
			} catch (SQLException e) {
				Logger.getInstance().log("Could not save to database!");
				Logger.getInstance().handleError(e);
			}
		}
	}

	public static void main(String[] args) {
		new DailyFollower(new File("DailyFollowerInfo")).run();

	}

}
