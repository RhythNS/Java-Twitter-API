package everyst.analytics.listner.twitter.events;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.parser.EventParser;
import everyst.analytics.listner.twitter.Tweet;
import everyst.analytics.listner.twitter.User;
import everyst.analytics.listner.twitter.database.DatabaseConstants;
import everyst.analytics.listner.twitter.database.InputTwitterObjects;
import everyst.analytics.mysql.MySQLConnection;

public class TweetCreateEvent extends Event {

	private enum Status {
		TWEET, RETWEET, REPLY, QUOTE;
	}

	private Tweet tweet, targetedStatus, quotedStatus;
	private User userWhoTweeted;
	private User[] mentions;
	private String replyToStatus;
	private Status status = Status.TWEET;

	public TweetCreateEvent(String data, JSONObject JSON) {
		super(data);

		// get tweet
		tweet = EventParser.getTweetObject(JSON);
		if (tweet == null) {
			errorOccured();
			return;
		}

		// get user
		try {
			JSONObject userJSON = JSON.getJSONObject("user");
			if (userJSON != null) {
				userWhoTweeted = EventParser.getUserObject(userJSON);
				if (userWhoTweeted == null) {
					errorOccured();
					return;
				}
			}
		} catch (JSONException e) {
			errorOccured();
			return;
		}

		// get mentions
		try {
			JSONObject entities = JSON.getJSONObject("entities");
			if (entities != null) {
				JSONArray mentions = entities.getJSONArray("user_mentions");
				if (mentions != null) {
					this.mentions = EventParser.getMentions(mentions);
				}
			}
		} catch (Exception e) {
		}

		// See what type of Tweet create event it was
		try { // Retweet
			JSONObject retweet = JSON.getJSONObject("retweeted_status");
			if (retweet != null) {
				targetedStatus = EventParser.getTweetObject(retweet);
				if (targetedStatus == null) {
					errorOccured();
					return;
				}
				status = Status.RETWEET;
				return;
			}
		} catch (JSONException e) { // dont need to do anything if it wasnt found
		}

		try { // Quote
			JSONObject quoteJSON = JSON.getJSONObject("quoted_status");
			if (quoteJSON != null) {
				quotedStatus = EventParser.getTweetObject(quoteJSON);
				if (quotedStatus != null) {
					status = Status.QUOTE;
					return;
				}
				errorOccured();
				return;
			}
		} catch (JSONException e) {
		}

		try { // Reply
			replyToStatus = JSON.getString("in_reply_to_status_id_str");
			if (replyToStatus != null) {
				status = Status.REPLY;
				return;
			}
		} catch (JSONException e) {
		}

	}

	@Override
	public void doTransaction(MySQLConnection database) throws SQLException {
		switch (status) {
		case QUOTE:
			EventUtil.tweetTweetInteraction(database, tweet, quotedStatus, DatabaseConstants.QUOTE);
			break;
		case REPLY:
			InputTwitterObjects.inputTweet(tweet, database);

			PreparedStatement statement = database
					.getStatement("INSERT INTO Tweet_Interacts_Tweet (FromID, ToID, Type) VALUES (?,?,?)");
			statement.setLong(1, Long.parseLong(tweet.getId()));
			statement.setLong(2, Long.parseLong(replyToStatus));
			statement.setByte(3, DatabaseConstants.REPLY);

			database.execute(statement);
			break;
		case RETWEET:
			EventUtil.userTweetInteraction(database, targetedStatus, userWhoTweeted, tweet.getCreatedAt(), DatabaseConstants.RETWEET);
			return;
		case TWEET:
			InputTwitterObjects.inputTweet(tweet, database);
			break;
		default:
			throw new SQLException("Status: " + status + " has not yet been implmented!");
		}
		
		for (int i = 0; i < mentions.length; i++) {
			EventUtil.tweetUserInteraction(database, tweet, mentions[i], DatabaseConstants.MENTION, false);
		}
		
	}

}
