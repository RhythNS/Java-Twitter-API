package everyst.analytics.listner.twitter.database;

import java.sql.SQLException;

import everyst.analytics.mysql.MySQLConnection;
import everyst.analytics.listner.dataManagement.Logger;

public abstract class InitDatabase {

	/*
		Please note that I am not that familiar with Database Structure and MySql.
		There might be some obvious mistakes in the design. 
		
		CREATE TABLE Tweet_Engagement (ID int UNSIGNED AUTO_INCREMENT, Quotes MEDIUMINT UNSIGNED, Replys MEDIUMINT UNSIGNED, Retweets MEDIUMINT UNSIGNED, Favourites MEDIUMINT UNSIGNED, PRIMARY KEY(ID));

		CREATE TABLE Tweet_Interacts_Tweet_Type (Name varchar(15), Type TINYINT(3) UNSIGNED, PRIMARY KEY (Type));
		INSERT INTO Tweet_Interacts_Tweet_Type (Name, Type) VALUES ('Quote', 0);
		INSERT INTO Tweet_Interacts_Tweet_Type (Name, Type) VALUES ('Reply', 1);

		CREATE TABLE User (ID BIGINT(19) UNSIGNED, Screen_Name varchar(15), Followers int UNSIGNED, PRIMARY KEY (ID));

		CREATE TABLE User_Interacts_User_Type (Name varchar(10), Type TINYINT(3) UNSIGNED, PRIMARY KEY(Type));
		INSERT INTO User_Interacts_User_Type (Name, Type) VALUES ('Follow', 0);
		INSERT INTO User_Interacts_User_Type (Name, Type) VALUES ('Block', 1);
		INSERT INTO User_Interacts_User_Type (Name, Type) VALUES ('Mute', 2);

		CREATE TABLE User_Interacts_User (FromID BIGINT(19) UNSIGNED, ToID BIGINT(19) UNSIGNED, Date TIMESTAMP, Type TINYINT(3) UNSIGNED, FOREIGN KEY (FromID) REFERENCES User (ID), FOREIGN KEY (ToID) REFERENCES User (ID), FOREIGN KEY (Type) REFERENCES User_Interacts_User_Type (Type));

		CREATE TABLE User_Interacts_Tweet_Type (Name varchar(10), Type TINYINT(3) UNSIGNED, PRIMARY KEY (Type));
		INSERT INTO User_Interacts_Tweet_Type (Name, Type) VALUES ('Favourite', 0);
		INSERT INTO User_Interacts_Tweet_Type (Name, Type) VALUES ('Retweet', 1);

		CREATE TABLE Tweet (ID BIGINT(19) UNSIGNED, FromID BIGINT(19) UNSIGNED, Date TIMESTAMP, Text varchar(280), EngagementID int UNSIGNED, PRIMARY KEY (ID), FOREIGN KEY (FromID) REFERENCES User (ID), FOREIGN KEY(EngagementID) REFERENCES Tweet_Engagement(ID));

		CREATE TABLE Tweet_Interacts_User_Type (Name varchar(10), Type TINYINT(19) UNSIGNED, PRIMARY KEY(Type));
		INSERT INTO Tweet_Interacts_User_Type (Name, Type) VALUES ('Mention', 0);
				
		CREATE TABLE (TweetID BIGINT(19) UNSIGNED, UserID BIGINT(19) UNSIGNED, Type TINYINT(3) UNSIGNED, FOREIGN KEY (TweetID) REFERENCES Tweet(ID), FOREIGN KEY (UserID) REFERENCES User (ID), FOREIGN KEY (Type) REFERENCES Tweet_Interacts_User_Type (Type));				

		CREATE TABLE Tweet_Interacts_Tweet (FromID BIGINT(19) UNSIGNED, ToID BIGINT(19) UNSIGNED, Type TINYINT(3) UNSIGNED, FOREIGN KEY (Type) REFERENCES Tweet_Interacts_Tweet_Type (Type));

		CREATE TABLE User_Interacts_Tweet (TweetID BIGINT(19) UNSIGNED, UserID BIGINT(19) UNSIGNED, Date TIMESTAMP, Type TINYINT(3) UNSIGNED, FOREIGN KEY (TweetID) REFERENCES Tweet(ID), FOREIGN KEY (UserID) REFERENCES User (ID), FOREIGN KEY (Type) REFERENCES User_Interacts_Tweet_Type (Type));		
	 */
	
	public static boolean init(MySQLConnection database) {
		String[] queries = {
				"CREATE TABLE Tweet_Engagement (ID int UNSIGNED AUTO_INCREMENT, Quotes MEDIUMINT UNSIGNED, Replys MEDIUMINT UNSIGNED, Retweets MEDIUMINT UNSIGNED, Favourites MEDIUMINT UNSIGNED, PRIMARY KEY(ID));",
				
				"CREATE TABLE Tweet_Interacts_Tweet_Type (Name varchar(15), Type TINYINT(3) UNSIGNED, PRIMARY KEY (Type));",
				"INSERT INTO Tweet_Interacts_Tweet_Type (Name, Type) VALUES ('Quote', 0);",
				"INSERT INTO Tweet_Interacts_Tweet_Type (Name, Type) VALUES ('Reply', 1);",
				
				"CREATE TABLE User (ID BIGINT(19) UNSIGNED, Screen_Name varchar(15), Followers int UNSIGNED, PRIMARY KEY (ID));",
				
				"CREATE TABLE User_Interacts_User_Type (Name varchar(10), Type TINYINT(3) UNSIGNED, PRIMARY KEY(Type));",
				"INSERT INTO User_Interacts_User_Type (Name, Type) VALUES ('Follow', 0);",
				"INSERT INTO User_Interacts_User_Type (Name, Type) VALUES ('Block', 1);",
				"INSERT INTO User_Interacts_User_Type (Name, Type) VALUES ('Mute', 2);",
				
				"CREATE TABLE User_Interacts_User (FromID BIGINT(19) UNSIGNED, ToID BIGINT(19) UNSIGNED, Date TIMESTAMP, Type TINYINT(3) UNSIGNED, FOREIGN KEY (FromID) REFERENCES User (ID), FOREIGN KEY (ToID) REFERENCES User (ID), FOREIGN KEY (Type) REFERENCES User_Interacts_User_Type (Type));",
				
				"CREATE TABLE User_Interacts_Tweet_Type (Name varchar(10), Type TINYINT(3) UNSIGNED, PRIMARY KEY (Type));",
				"INSERT INTO User_Interacts_Tweet_Type (Name, Type) VALUES ('Favourite', 0);",
				"INSERT INTO User_Interacts_Tweet_Type (Name, Type) VALUES ('Retweet', 1);",
				
				"CREATE TABLE Tweet (ID BIGINT(19) UNSIGNED, FromID BIGINT(19) UNSIGNED, Date TIMESTAMP, Text varchar(280), EngagementID int UNSIGNED, PRIMARY KEY (ID), FOREIGN KEY (FromID) REFERENCES User (ID), FOREIGN KEY(EngagementID) REFERENCES Tweet_Engagement(ID));",
				
				"CREATE TABLE Tweet_Interacts_User_Type (Name varchar(10), Type TINYINT(19) UNSIGNED, PRIMARY KEY(Type));",
				"INSERT INTO Tweet_Interacts_User_Type (Name, Type) VALUES ('Mention', 0);",
				
				"CREATE TABLE Tweet_Interacts_User (TweetID BIGINT(19) UNSIGNED, UserID BIGINT(19) UNSIGNED, Type TINYINT(3) UNSIGNED, FOREIGN KEY (TweetID) REFERENCES Tweet(ID), FOREIGN KEY (UserID) REFERENCES User (ID), FOREIGN KEY (Type) REFERENCES Tweet_Interacts_User_Type (Type));",
				
				"CREATE TABLE Tweet_Interacts_Tweet (FromID BIGINT(19) UNSIGNED, ToID BIGINT(19) UNSIGNED, Type TINYINT(3) UNSIGNED, FOREIGN KEY (Type) REFERENCES Tweet_Interacts_Tweet_Type (Type));",

				"CREATE TABLE User_Interacts_Tweet (TweetID BIGINT(19) UNSIGNED, UserID BIGINT(19) UNSIGNED, Date TIMESTAMP, Type TINYINT(3) UNSIGNED, FOREIGN KEY (TweetID) REFERENCES Tweet(ID), FOREIGN KEY (UserID) REFERENCES User (ID), FOREIGN KEY (Type) REFERENCES User_Interacts_Tweet_Type (Type));" 
			};
		
		for (String string : queries) {
			try {
				database.execute(string);
			} catch (SQLException e) {
				Logger.getInstance().log("Could not create the tables!");
				Logger.getInstance().handleError(e);
				return false;
			}
		} // end for
		return true;
	}

}
