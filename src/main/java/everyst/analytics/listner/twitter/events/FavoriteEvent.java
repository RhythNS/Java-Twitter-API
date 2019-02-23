package everyst.analytics.listner.twitter.events;

import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.parser.EventParser;
import everyst.analytics.listner.twitter.Tweet;
import everyst.analytics.listner.twitter.User;

public class FavoriteEvent extends Event {

	private User whoFavorited;
	private Tweet tweet;

	public FavoriteEvent(String data, JSONObject json) {
		super(data);

		JSONObject tweetJSON, userJSON;
		try {
			tweetJSON = json.getJSONObject("favorited_status");
			userJSON = json.getJSONObject("user");
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
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
