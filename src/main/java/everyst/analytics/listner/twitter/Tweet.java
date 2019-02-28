package everyst.analytics.listner.twitter;

import java.time.LocalDateTime;

public class Tweet {
	
	private String id;
	private User user;
	private LocalDateTime createdAt;
	private String text;
	private TweetEngagement tweetEngagement;
	
	public Tweet(String id, User user, LocalDateTime createdAt, String text, TweetEngagement tweetEngagement) {
		this.id = id;
		this.user = user;
		this.createdAt = createdAt;
		this.text = text;
		this.tweetEngagement = tweetEngagement;
	}

	public String getId() {
		return id;
	}
	
	public User getUser() {
		return user;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public String getText() {
		return text;
	}

	public TweetEngagement getTweetEngagement() {
		return tweetEngagement;
	}
	
	


	
}
