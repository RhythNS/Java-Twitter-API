package everyst.analytics.smallHelpers.dailyFollower;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {

	public static void main(String[] args) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/javaTest?" + "user=java&password=password");
		Statement statement = conn.createStatement();

		System.out.println(statement
				.execute("CREATE TABLE User (ID int NOT NULL, Name varchar(255), Age int, PRIMARY KEY (ID));"));

		
		conn.close();
	}

}
