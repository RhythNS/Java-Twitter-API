package everyst.analytics.listner.twitter.events;

import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;

public class TweetDeleteEvent extends Event{
	
	private String userID;
	private String tweetID;

	public TweetDeleteEvent(String data, JSONObject JSON) {
		super(data);
		
		try {
			JSONObject status = JSON.getJSONObject("status");
			
			userID = status.getString("id");
			tweetID = status.getString("user_id");
		} catch (JSONException e) {
			Logger.getInstance().handleError(e);
			errorOccured();
		}
		
	}
	
	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
