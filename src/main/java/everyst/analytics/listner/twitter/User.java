package everyst.analytics.listner.twitter;

public class User {

	private String id;
	private String screenName;
	private String followersCount;

	public User(String id, String screenName, String followersCount) {
		this.id = id;
		this.screenName = screenName;
		this.followersCount = followersCount;
	}

	public String getId() {
		return id;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getFollowersCount() {
		return followersCount;
	}

}
