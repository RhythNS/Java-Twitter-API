package everyst.analytics.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import everyst.analytics.smallHelpers.twitterFollowerTracker.Logger;

public class MySQLConnection {

	private Connection connection;

	public MySQLConnection(String database, String user, String password) throws SQLException {
		connection = DriverManager
				.getConnection("jdbc:mysql://localhost/" + database + "?user=" + user + "&password=" + password);
	}

	public ResultSet execute(String query) throws SQLException {
		if (isClosed()) {
			throw new IllegalStateException("Connection already closed!");
		}
		Statement statement = connection.createStatement();
		if (statement.execute(query))
			return statement.getResultSet();
		return null;
	}

	public boolean isClosed() {
		try {
			return connection.isClosed();
		} catch (SQLException e) {
			return true;
		}
	}

	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			Logger.getInstance().handleError(e);
			e.printStackTrace();
		}
	}

}
