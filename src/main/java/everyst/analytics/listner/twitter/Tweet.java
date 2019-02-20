package everyst.analytics.listner.twitter;

import java.time.LocalDateTime;

import everyst.analytics.listner.utility.MySQLable;

public class Tweet implements MySQLable{
	
	private String id;
	private LocalDateTime createdAt;
	private String text;
	private TweetEngagement tweetEngagement;
	
	public Tweet(String id, LocalDateTime createdAt, String text, TweetEngagement tweetEngagement) {
		this.id = id;
		this.createdAt = createdAt;
		this.text = text;
		this.tweetEngagement = tweetEngagement;
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
