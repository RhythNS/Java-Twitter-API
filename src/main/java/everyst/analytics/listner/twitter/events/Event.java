package everyst.analytics.listner.twitter.events;

import java.sql.SQLException;

import everyst.analytics.mysql.MySQLConnection;

public abstract class Event {

	private String data;
	private boolean error;

	public Event(String data) {
		this.data = data;
		error = false;
	}

	public abstract void doTransaction(MySQLConnection database) throws SQLException;

	/**
	 * Returns the raw message gotten through the Webhook.
	 */
	public String getData() {
		return data;
	}

	protected void errorOccured() {
		error = true;
	}

	public boolean isError() {
		return error;
	}

}
