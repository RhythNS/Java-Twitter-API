package everyst.analytics.listner.twitter.events;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDateTime;

import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.twitter.Tweet;
import everyst.analytics.listner.twitter.User;
import everyst.analytics.listner.twitter.database.InputTwitterObjects;
import everyst.analytics.listner.utility.TimeUtility;
import everyst.analytics.mysql.MySQLConnection;

public class EventUtil {

	public static void userUserInteraction(MySQLConnection database, User source, User target, long time, byte number)
			throws SQLException {
		InputTwitterObjects.inputUser(source, database);
		InputTwitterObjects.inputUser(target, database);

		PreparedStatement statement = database.getStatement(
				"INSERT INTO User_Interacts_User (FromID, ToID, Date, Type) VALUES (?,?, FROM_UNIXTIME(?/1000), ?);");
		statement.setLong(1, Long.parseLong(source.getId()));
		statement.setLong(2, Long.parseLong(target.getId()));
		statement.setLong(3, time);
		statement.setByte(4, number);

		database.execute(statement);
	}

	public static void userTweetInteraction(MySQLConnection database, Tweet tweet, User user, long time, byte number)
			throws SQLException {
		InputTwitterObjects.inputTweet(tweet, database);
		InputTwitterObjects.inputUser(user, database);

		PreparedStatement statement = database.getStatement(
				"INSERT INTO User_Interacts_Tweet (TweetID, UseriD, Date, Type) VALUES (?,?, FROM_UNIXTIME(?/1000), ?);");
		statement.setLong(1, Long.parseLong(tweet.getId()));
		statement.setLong(2, Long.parseLong(user.getId()));
		statement.setLong(3, time);
		statement.setByte(4, number);

		database.execute(statement);
	}

	public static void userTweetInteraction(MySQLConnection database, Tweet tweet, User user, LocalDateTime time,
			byte number) throws SQLException {
		InputTwitterObjects.inputTweet(tweet, database);
		InputTwitterObjects.inputUser(user, database);

		PreparedStatement statement = database
				.getStatement("INSERT INTO User_Interacts_Tweet (TweetID, UseriD, Date, Type) VALUES (?,?,?,?);");
		statement.setLong(1, Long.parseLong(tweet.getId()));
		statement.setLong(2, Long.parseLong(user.getId()));
		statement.setTimestamp(3, Timestamp.valueOf(time));
		statement.setByte(4, number);

		database.execute(statement);

	}

	public static void tweetTweetInteraction(MySQLConnection database, Tweet source, Tweet target, byte number)
			throws SQLException {
		InputTwitterObjects.inputTweet(source, database);
		InputTwitterObjects.inputTweet(target, database);

		PreparedStatement statement = database
				.getStatement("INSERT INTO Tweet_Interacts_Tweet (FromID, ToID, Type) VALUES (?,?,?)");
		statement.setLong(1, Long.parseLong(source.getId()));
		statement.setLong(2, Long.parseLong(target.getId()));
		statement.setByte(3, number);

		database.execute(statement);
	}

	public static void tweetUserInteraction(MySQLConnection database, Tweet tweet, User user, byte number,
			boolean updateFollowerNumberForTarget) throws SQLException {
		InputTwitterObjects.inputTweet(tweet, database);
		if (updateFollowerNumberForTarget)
			InputTwitterObjects.inputUser(user, database);
		else
			InputTwitterObjects.inputUserWithoutUpdating(user, database);

		PreparedStatement statement = database
				.getStatement("INSERT INTO Tweet_Interacts_User (TweetID, UserID, Type) VALUES (?,?,?)");
		statement.setLong(1, Long.parseLong(tweet.getId()));
		statement.setLong(2, Long.parseLong(user.getId()));
		statement.setByte(3, number);

		database.execute(statement);
	}

	public static LocalDateTime getTime(JSONObject JSON, String key) throws JSONException {
		try {
			// get the time from the JSON
			String value = JSON.getString(key);

			// parse the String via TimeUtility
			return TimeUtility.parseFromApi(value);
		} catch (DateTimeException e) {
			throw new JSONException("Could not parse time: " + e.getMessage());
		}
	}

}
