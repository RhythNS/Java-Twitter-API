package everyst.analytics.listner.twitter.events;

import everyst.analytics.listner.twitter.Tweet;
import everyst.analytics.listner.twitter.User;

public class FavoriteEvent extends Event{

	private User whoFavorited;
	private Tweet tweet;
	
	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
