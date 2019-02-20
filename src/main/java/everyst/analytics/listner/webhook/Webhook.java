package everyst.analytics.listner.webhook;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import org.json.JSONObject;

import everyst.analytics.listner.App;
import everyst.analytics.listner.KeyManager;
import everyst.analytics.listner.dataManagement.queueWriter.Type;
import fi.iki.elonen.NanoHTTPD;

public class Webhook extends NanoHTTPD {

	private CRCResponse crcResponse;
	private Queue<String> jsonQueue;
	private App app;

	public Webhook(Queue<String> jsonQueue, KeyManager keymanager, App app) {
		// Init the Webhook with the Server Hostname and port
		super(WebConstants.HOSTNAME, WebConstants.PORT);
		this.app = app;

		this.jsonQueue = jsonQueue;
		crcResponse = new CRCResponse(keymanager);

		// Enable required SSLProtocols
		System.setProperty("sslEnabledProtocols", "TLSv1.2,TLSv1.1,TLSv1");

		// Enable javax.net debug mode if the App is started in debug mode
		if (App.SERVER_PROTOCOL_DEBUG)
			System.setProperty("javax.net.debug", "all");
	}

	public void start(String password) throws IOException {
		// Passes keystore.jks which should be next to the jar to Nanohttpd

//		String protocols[] = {"TLSv1.2", "TLSv1.1", "TLSv1"};

		makeSecure(NanoHTTPD.makeSSLSocketFactory("/keystore.jks", password.toCharArray()), null);

		// Start the internal server thread
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
	}

	@Override
	public Response serve(IHTTPSession session) {
		Map<String, String> parameters = session.getParms();

		// check if the parameters contain a crc challenge parameter
		Object crc = parameters.get(WebConstants.CRC_TOKEN_REQUEST_PARAMETER_KEY);
		if (crc != null) // is a crc challenge -> return the answer
			return doCRCCheck((String) crc);

		for (String string : parameters.values()) {
			jsonQueue.add(string);
		}

		if (App.DEBUG)
			System.out.println(parameters.toString());

		return SampleResponses.getOkay();
	}

	private Response doCRCCheck(String token) {
		if (!(token instanceof String)) {
			return SampleResponses.getInternalError();
		}

		String crcResponce = crcResponse.getChallengeResponse(token);

		if (crcResponce == null)
			return SampleResponses.getInternalError();

		JSONObject response = new JSONObject();
		response.put(WebConstants.CRC_TOKEN_RESPONSE_PARAMETER_KEY, crcResponce);

		return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, response.toString());
	}

}
