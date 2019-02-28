package everyst.analytics.listner.twitter.events;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.mysql.MySQLConnection;

public class TweetDeleteEvent extends Event {

	private String tweetID;

	public TweetDeleteEvent(String data, JSONObject JSON) {
		super(data);

		try {
			JSONObject status = JSON.getJSONObject("status");

			tweetID = status.getString("user_id");
		} catch (JSONException e) {
			Logger.getInstance().handleError(e);
			errorOccured();
		}
	}

	@Override
	public void doTransaction(MySQLConnection database) throws SQLException {
		PreparedStatement statement = database.getStatement("UPDATE Tweet SET Text=? WHERE ID=?");
		statement.setString(1, "DELETED");
		statement.setLong(2, Long.parseLong(tweetID));

		database.execute(statement);
	}

}
