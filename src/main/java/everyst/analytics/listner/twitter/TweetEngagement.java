package everyst.analytics.listner.twitter;

public class TweetEngagement {

	String impressions;
	String engagements;
	String quoteCount;
	String replyCount;
	String retweetCount;
	String favoriteCount;

	public TweetEngagement(String impressions, String engagements, String quoteCount, String replyCount, String retweetCount,
			String favoriteCount) {
		this.impressions = impressions;
		this.engagements = engagements;
		this.quoteCount = quoteCount;
		this.replyCount = replyCount;
		this.retweetCount = retweetCount;
		this.favoriteCount = favoriteCount;
	}

}
