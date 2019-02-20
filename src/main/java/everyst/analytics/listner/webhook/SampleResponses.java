package everyst.analytics.listner.webhook;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public class SampleResponses {

	public static Response getInternalError() {
		return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT,
				"INTERNAL SERVER ERROR");
	}

	public static Response getUnknownError() {
		return NanoHTTPD.newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT,
				"COMMAND UNKNOWN");
	}

	public static Response getOkay() {
		return NanoHTTPD.newFixedLengthResponse("<html><body>OK</body></html>\n\n");
	}

}
