package everyst.analytics.listner.twitter;

public class TweetEngagement {

	private String quoteCount;
	private String replyCount;
	private String retweetCount;
	private String favoriteCount;

	public TweetEngagement(String quoteCount, String replyCount, String retweetCount,
			String favoriteCount) {
		this.quoteCount = quoteCount;
		this.replyCount = replyCount;
		this.retweetCount = retweetCount;
		this.favoriteCount = favoriteCount;
	}

	public String getQuoteCount() {
		return quoteCount;
	}

	public String getReplyCount() {
		return replyCount;
	}

	public String getRetweetCount() {
		return retweetCount;
	}

	public String getFavoriteCount() {
		return favoriteCount;
	}
	
	

}
