package everyst.analytics.listner.twitter;

import everyst.analytics.listner.utility.MySQLable;

public class User implements MySQLable {

	private String id;
	private String screenName;
	private String followersCount;

	public User(String id, String screenName, String followersCount) {
		this.id = id;
		this.screenName = screenName;
		this.followersCount = followersCount;
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
