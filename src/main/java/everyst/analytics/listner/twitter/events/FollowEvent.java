package everyst.analytics.listner.twitter.events;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.parser.EventParser;
import everyst.analytics.listner.twitter.User;
import everyst.analytics.listner.twitter.database.DatabaseConstants;
import everyst.analytics.mysql.MySQLConnection;

public class FollowEvent extends Event {

	private User source, target;
	private long time;

	public FollowEvent(String data, JSONObject JSON) {
		super(data);

		JSONObject sourceJSON, targetJSON;
		try {
			sourceJSON = JSON.getJSONObject("source");
			targetJSON = JSON.getJSONObject("target");
			time = JSON.getLong("created_timestamp");
		} catch (JSONException e) {
			Logger.getInstance().handleError(e);
			errorOccured();
			return;
		}

		source = EventParser.getUserObject(sourceJSON);
		if (source == null) {
			errorOccured();
			return;
		}

		target = EventParser.getUserObject(targetJSON);
		if (target == null)
			errorOccured();
	}

	@Override
	public void doTransaction(MySQLConnection database) throws SQLException {
		EventUtil.userUserInteraction(database, source, target, time, DatabaseConstants.FOLLOW);
	}

}
