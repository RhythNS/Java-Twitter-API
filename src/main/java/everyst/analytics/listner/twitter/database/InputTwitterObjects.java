package everyst.analytics.listner.twitter.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import everyst.analytics.listner.twitter.Tweet;
import everyst.analytics.listner.twitter.TweetEngagement;
import everyst.analytics.listner.twitter.User;
import everyst.analytics.mysql.MySQLConnection;

public class InputTwitterObjects {
	
	/**
	 * Tries to input user. Throws SQLException if something went wrong
	 */
	public static void inputUser(User user, MySQLConnection connection) throws SQLException {
		// get the statement
		PreparedStatement statement = connection.getStatement(
				"INSERT INTO User (ID, Screen_Name, Followers) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Screen_Name=?, Followers=?;");

		// set all parameters
		statement.setLong(1, Long.parseLong(user.getId()));
		statement.setString(2, user.getScreenName());
		int followerCount = Integer.parseInt(user.getFollowersCount());
		statement.setInt(3, followerCount);
		statement.setString(4, user.getScreenName());
		statement.setInt(5, followerCount);

		// execute it
		connection.execute(statement);
	}

	/**
	 * Tries to input user. Throws SQLException if something went wrong
	 */
	public static void inputUserWithoutUpdating(User user, MySQLConnection connection) throws SQLException {
		// get the statement
		PreparedStatement statement = connection.getStatement(
				"INSERT INTO User (ID, Screen_Name, Followers) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Screen_Name=?;");

		// set all parameters
		statement.setLong(1, Long.parseLong(user.getId()));
		statement.setString(2, user.getScreenName());
		int followerCount = Integer.parseInt(user.getFollowersCount());
		statement.setInt(3, followerCount);
		statement.setString(4, user.getScreenName());

		// execute it
		connection.execute(statement);
	}

	/**
	 * Tries to input Tweet. Throws SQLException if something went wrong
	 */
	public static void inputTweet(Tweet tweet, MySQLConnection connection) throws SQLException {
		// get the statment
		ResultSet res = connection.execute("SELECT EngagementID FROM Tweet WHERE ID=" + tweet.getId() + ";");

		if (!res.next()) { // Tweet is new
			// input user
			inputUser(tweet.getUser(), connection);

			// get the statement for the tweet_engagment object
			PreparedStatement statement = connection.getStatement(
					"INSERT INTO Tweet_Engagement (Quotes, Replys, Retweets, Favourites) VALUES (?, ?, ?, ?);");

			// fill the statement
			fillEngagements(statement, tweet.getTweetEngagement());

			// execute it
			connection.execute(statement);

			// get the statment for the tweet object
			statement = connection.getStatement(
					"INSERT INTO Tweet (ID, FromID, Date, Text, EngagementID) VALUES (?, ?, ?, ?, LAST_INSERT_ID());");

			// fill the statement
			statement.setLong(1, Long.parseLong(tweet.getId()));
			statement.setLong(2, Long.parseLong(tweet.getUser().getId()));
			statement.setTimestamp(3, Timestamp.valueOf(tweet.getCreatedAt()));
			statement.setString(4, tweet.getText());
			
			// execute it
			connection.execute(statement);
		} else { // Tweet is not new
			// get the statement for updating the engagement
			PreparedStatement statement = connection.getStatement(
					"UPDATE Tweet_Engagement SET Quotes=?, Replys=?, Retweets=?, Favourites=? WHERE ID=?;");

			// fill the statement
			fillEngagements(statement, tweet.getTweetEngagement());
			statement.setInt(5, res.getInt(1));

			// execute it
			connection.execute(statement);
		}
	}

	private static void fillEngagements(PreparedStatement statement, TweetEngagement engagement) throws SQLException {
		try {
			statement.setInt(1, Integer.parseInt(engagement.getQuoteCount()));
			statement.setInt(2, Integer.parseInt(engagement.getReplyCount()));
			statement.setInt(3, Integer.parseInt(engagement.getRetweetCount()));
			statement.setInt(4, Integer.parseInt(engagement.getFavoriteCount()));			
		} catch (NumberFormatException e) {
			throw new SQLException("Number format exception: " + e.getMessage());
		}
	}

}
