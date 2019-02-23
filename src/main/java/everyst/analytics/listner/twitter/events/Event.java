package everyst.analytics.listner.twitter.events;

import everyst.analytics.listner.utility.MySQLable;

public abstract class Event implements MySQLable {

	private String data;
	private boolean error;

	public Event(String data) {
		this.data = data;
		error = false;
	}

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
