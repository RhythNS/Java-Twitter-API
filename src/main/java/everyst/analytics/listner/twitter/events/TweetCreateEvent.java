package everyst.analytics.listner.twitter.events;

import org.json.JSONObject;

import everyst.analytics.listner.twitter.Tweet;

public class TweetCreateEvent extends Event{
	
	private Tweet tweet;

	public TweetCreateEvent(String data, JSONObject JSON) {
		super(data);
	}


	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
