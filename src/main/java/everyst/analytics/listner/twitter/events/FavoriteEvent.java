package everyst.analytics.listner.twitter.events;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.parser.EventParser;
import everyst.analytics.listner.twitter.Tweet;
import everyst.analytics.listner.twitter.User;
import everyst.analytics.listner.twitter.database.DatabaseConstants;
import everyst.analytics.mysql.MySQLConnection;

public class FavoriteEvent extends Event {

	private User whoFavorited;
	private Tweet tweet;
	private long time;

	public FavoriteEvent(String data, JSONObject json) {
		super(data);

		JSONObject tweetJSON, userJSON;
		try {
			tweetJSON = json.getJSONObject("favorited_status");
			userJSON = json.getJSONObject("user");
			time = json.getLong("timestamp_ms");
		} catch (JSONException e) {
			Logger.getInstance().handleError(e);
			errorOccured();
			return;
		}

		tweet = EventParser.getTweetObject(tweetJSON);
		if (tweet == null) {
			errorOccured();
			return;
		}

		whoFavorited = EventParser.getUserObject(userJSON);
		if (whoFavorited == null)
			errorOccured();
	}

	@Override
	public void doTransaction(MySQLConnection database) throws SQLException {
		EventUtil.userTweetInteraction(database, tweet, whoFavorited, time, DatabaseConstants.FAVOURITE);
	}
		

}
