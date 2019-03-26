package everyst.analytics.listner.webhook;

import everyst.analytics.listner.App;

public class WebConstants {

	// Connection
	public static final int PORT = 9090;
	public static final String HOSTNAME = App.DEBUG ? "localhost" : "analytics.fromeveryst.com";

	public static final String CRC_TOKEN_REQUEST_PARAMETER_KEY = "crc_token";
	public static final String CRC_TOKEN_RESPONSE_PARAMETER_KEY = "response_token";

}
