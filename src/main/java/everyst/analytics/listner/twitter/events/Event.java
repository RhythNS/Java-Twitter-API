package everyst.analytics.listner.twitter.events;

import everyst.analytics.listner.utility.MySQLable;

public abstract class Event implements MySQLable {

	private String data;

	/**
	 * Returns the raw message gotten through the Webhook.
	 */
	public String getData() {
		return data;
	}

}
