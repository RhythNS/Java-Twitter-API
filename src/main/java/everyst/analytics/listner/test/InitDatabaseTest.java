package everyst.analytics.listner.test;

import java.sql.SQLException;

import everyst.analytics.listner.twitter.database.InitDatabase;
import everyst.analytics.mysql.MySQLConnection;

public class InitDatabaseTest {
	
	public static void main(String[] args) {
		try {
			System.out.println(InitDatabase.init(new MySQLConnection("javaTest", "java", "password")));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
