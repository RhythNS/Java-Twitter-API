package everyst.analytics.listner.userInterface;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;

import everyst.analytics.listner.App;
import everyst.analytics.mysql.MySQLConnection;

public class Commands {

	private MySQLConnection conn;
	private App app;

	public Commands(MySQLConnection conn, App app) {
		this.conn = conn;
		this.app = app;
	}

	/**
	 * https://stackoverflow.com/a/74763
	 */
	public String getStatus() {
		Runtime runtime = Runtime.getRuntime();

		NumberFormat format = NumberFormat.getInstance();

		StringBuilder sb = new StringBuilder();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();

		sb.append("Free memory: " + format.format(freeMemory / 1024) + "\n");
		sb.append("Allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
		sb.append("Max memory: " + format.format(maxMemory / 1024) + "\n");
		sb.append("Total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
		sb.append("StringWorker alive: " + app.isStringWorkerAlive() + "\n");
		sb.append("EventWorker alive: " + app.isEventWorkerAlive() + "\n");
		sb.append("TaskManager alive: " + app.getTaskManager().isAlive() + "\n");

		return sb.toString();
	}

	public String getLast5LikeDates() throws SQLException {
		ResultSet res = conn.execute("SELECT Date FROM User_Interacts_Tweet ORDER BY Date DESC LIMIT 5");
		res.next();
		StringBuilder sb = new StringBuilder(res.getString(1));

		for (int i = 0; i < 4 && res.next(); i++) {
			sb.append("\n").append(res.getString(1));
		}
		return sb.toString();
	}

}
