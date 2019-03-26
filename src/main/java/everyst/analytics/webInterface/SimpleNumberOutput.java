package everyst.analytics.webInterface;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import everyst.analytics.listner.webhook.URLListner;
import everyst.analytics.mysql.MySQLConnection;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public class SimpleNumberOutput implements URLListner {

	private MySQLConnection connection;
	private final static String PATH = "/WebInterface/SimpleNumberOutput",
			BEGIN_TABLE = "<table style=\"width:80%\">", END_TABLE = "</tr></table>";

	public SimpleNumberOutput(MySQLConnection connection) {
		this.connection = connection;
	}

	@Override
	public String getPath() {
		return PATH;
	}
	
	/**
	 * It is ugly. It is not optimal but it works  ¯\_(ツ)_/¯
	 */

	@Override
	public Response getResponse(Map<String, String> parameters) {

		String from = parameters.get("from"), to = parameters.get("to"), account = parameters.get("account");

		StringBuilder response = new StringBuilder();
		response.append(ResponseConstants.PRE);

		// if the attributes are present attach all tables to the bottom of the page
		if (from != null && to != null && account != null) {

			boolean isSameDay = from.equals(to);
			try {
				StringBuilder tableString = new StringBuilder();
				LocalDate fromDate = getDate(from), toDate = getDate(to);

				tableString.append("<header>Unique likers and total likes</header>");

				ResultSet res = getStandardSet("Select Count(DISTINCT Likers.ID) as UniqueLikers, COUNT(*) as Likes"
						+ " FROM Tweet" + " Inner join User as Tweeter on Tweeter.ID = Tweet.FromID"
						+ " Inner join User_Interacts_Tweet as Interact on Tweet.ID = Interact.TweetID"
						+ " Inner join User as Likers ON Likers.ID = Interact.UserID"
						+ " Where Tweeter.Screen_Name = ? AND Interact.Type = 0 AND" + appendDateSelection(isSameDay)
						+ ";", account, fromDate, toDate, isSameDay);

				tableString.append(BEGIN_TABLE);
				tableString.append("<tr><th>Unique Likers</th><th>Likes</th></tr>");
				while (res.next()) {
					tableString.append("<tr><td>");
					tableString.append(res.getInt(1));
					tableString.append("</td><td>");
					tableString.append(res.getInt(2));
					tableString.append("</td></tr>");
				}
				tableString.append(END_TABLE);

				tableString.append("<header>Highest Likers</header>");

				res = getStandardSet("SELECT Likers.Followers as Followers, Likers.Screen_Name, Count(*) AS Count"
						+ " FROM Tweet" + " Inner join User as Tweeter on Tweeter.ID = Tweet.FromID"
						+ " Inner join User_Interacts_Tweet as Interact on Tweet.ID = Interact.TweetID"
						+ " Inner join User as Likers ON Likers.ID = Interact.UserID"
						+ " WHERE Tweeter.Screen_Name = ? AND Interact.Type = 0 AND" + appendDateSelection(isSameDay)
						+ " GROUP BY Likers.ID" + " ORDER BY Count Desc limit 10;", account, fromDate, toDate,
						isSameDay);

				tableString.append(BEGIN_TABLE);
				tableString.append("<tr><th>Followers</th><th>Screen Name</th><th>Likes</th>");
				while (res.next()) {
					tableString.append("<tr><td>");
					tableString.append(res.getInt(1));
					tableString.append("</td><td>");
					tableString.append(res.getString(2));
					tableString.append("</td><td>");
					tableString.append(res.getInt(3));
					tableString.append("</td></tr>");
				}
				tableString.append(END_TABLE);

				tableString.append("<header>Unique Retweeters and total retweets</header>");

				res = getStandardSet("Select Count(DISTINCT Likers.ID) as UniqueRetweeter, COUNT(*) as Retweets"
						+ " FROM Tweet" + " Inner join User as Tweeter on Tweeter.ID = Tweet.FromID"
						+ " Inner join User_Interacts_Tweet as Interact on Tweet.ID = Interact.TweetID"
						+ " Inner join User as Likers ON Likers.ID = Interact.UserID"
						+ " Where Tweeter.Screen_Name = ? AND Interact.Type = 1 AND" + appendDateSelection(isSameDay)
						+ ";", account, fromDate, toDate, isSameDay);

				tableString.append(BEGIN_TABLE);
				tableString.append("<tr><th>Unique Retweeters</th><th>Retweets</th></tr>");
				while (res.next()) {
					tableString.append("<tr><td>");
					tableString.append(res.getInt(1));
					tableString.append("</td><td>");
					tableString.append(res.getInt(2));
					tableString.append("</td></tr>");
				}
				tableString.append(END_TABLE);

				tableString.append("<header>How many people liked how much</header>");

				res = getStandardSet(
						"SELECT cnt as Liked_Tweets, COUNT(*) AS Count" + " FROM" + " (SELECT Count(*) as cnt"
								+ " From Tweet" + " Inner join User as Tweeter on Tweeter.ID = Tweet.FromID"
								+ " Inner join User_Interacts_Tweet as Interact on Tweet.ID = Interact.TweetID"
								+ " Inner join User as Likers ON Likers.ID = Interact.UserID"
								+ " Where Tweeter.Screen_Name = ? And" + appendDateSelection(isSameDay)
								+ " AND Interact.Type = 0" + " GROUP BY Likers.ID" + ") as grp" + " GROUP By cnt;",
						account, fromDate, toDate, isSameDay);

				tableString.append(BEGIN_TABLE);
				tableString.append("<tr><th>Liked Tweets</th><th>Amount of People</th></tr>");
				while (res.next()) {
					tableString.append("<tr><td>");
					tableString.append(res.getInt(1));
					tableString.append("</td><td>");
					tableString.append(res.getInt(2));
					tableString.append("</td></tr>");
				}
				tableString.append(END_TABLE);

				tableString.append("<header>Heighest liked tweets</header>");

				res = getStandardSet("SELECT Date, Text, Favourites, Retweets, Replys, Quotes" + " FROM Tweet"
						+ " Inner join User ON Tweet.FromID = User.ID"
						+ " Inner join Tweet_Engagement ON Tweet.EngagementID = Tweet_Engagement.ID"
						+ " Where Screen_Name=? And" + (isSameDay ? " Date = ?" : " Date BETWEEN ? AND ?")
						+ " ORDER by Favourites DESC LIMIT 5;", account, fromDate, toDate, isSameDay);

				tableString.append(BEGIN_TABLE);
				tableString.append(
						"<tr><th>Date</th><th>Text</th><th>Favourites</th><th>Retweets</th><th>Replies</th><th>Quotes</th></tr>");
				while (res.next()) {
					tableString.append("<tr><td>");
					tableString.append(res.getDate(1)); // date
					tableString.append("</td><td>");
					tableString.append(res.getString(2)); // text
					tableString.append("</td><td>");
					tableString.append(res.getInt(3)); // favourites
					tableString.append("</td><td>");
					tableString.append(res.getInt(4)); // retweets
					tableString.append("</td><td>");
					tableString.append(res.getInt(5)); // replies
					tableString.append("</td><td>");
					tableString.append(res.getInt(6)); // quotes
					tableString.append("</td></tr>");
				}
				tableString.append(END_TABLE);

				tableString.append("<header>Top gained followers</header>");

				res = getStandardSet(
						"SELECT Follower.Screen_Name, Follower.Followers" + " From User_Interacts_User as Interact"
								+ " Inner Join User as Follower on Follower.ID = Interact.FromID"
								+ " Inner Join User as Acc on Acc.ID = Interact.ToID"
								+ " Where Interact.Type = 0 AND Acc.Screen_Name = ? AND"
								+ appendDateSelection(isSameDay) + " ORDER by Follower.Followers DESC LIMIT 10;",
						account, fromDate, toDate, isSameDay);

				tableString.append(BEGIN_TABLE);
				tableString.append("<tr><th>Screen Name</th><th>Followers</th></tr>");
				while (res.next()) {
					tableString.append("<tr><td>");
					tableString.append(res.getString(1));
					tableString.append("</td><td>");
					tableString.append(res.getInt(2));
					tableString.append("</td></tr>");
				}
				tableString.append(END_TABLE);

				response.append(tableString);
			} catch (SQLException | DateTimeException e) {
				e.printStackTrace();
			}

		}

		response.append(ResponseConstants.AFTER);

		// finally put the response StringBuilder into an response and return it
		return NanoHTTPD.newFixedLengthResponse(response.toString());
	}

	private ResultSet getStandardSet(String query, String account, LocalDate from, LocalDate to, boolean isSameDay)
			throws SQLException {
		PreparedStatement statement = connection.getStatement(query);
		statement.setString(1, account);
		setDate(statement, from, to, isSameDay, 2);

		return connection.execute(statement);
	}

	private static String appendDateSelection(boolean isSameDay) {
		return isSameDay ? " Interact.Date = ?" : " Interact.Date BETWEEN ? AND ?";
	}

	private static void setDate(PreparedStatement statement, LocalDate from, LocalDate to, boolean isSameDay,
			int beginningIndex) throws SQLException {
		if (isSameDay) {
			statement.setObject(beginningIndex, from);
		} else {
			statement.setObject(beginningIndex, from);
			statement.setObject(beginningIndex + 1, to);
		}
	}

	private static LocalDate getDate(String date) throws DateTimeException {
		return LocalDate.parse(date, DateTimeFormatter.ofPattern("uuuu-MM-dd"));
	}

}
