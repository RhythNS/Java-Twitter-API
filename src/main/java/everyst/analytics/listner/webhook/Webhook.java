package everyst.analytics.listner.webhook;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import org.json.JSONObject;

import everyst.analytics.listner.App;
import everyst.analytics.listner.KeyManager;
import everyst.analytics.listner.dataManagement.Logger;
import fi.iki.elonen.NanoHTTPD;

public class Webhook extends NanoHTTPD {

	private CRCResponse crcResponse;
	private Queue<String> jsonQueue;

	public Webhook(BlockingQueue<String> stringQueue, KeyManager keymanager) {
		// Init the Webhook with the Server Hostname and port
		super(WebConstants.HOSTNAME, WebConstants.PORT);

		this.jsonQueue = stringQueue;
		crcResponse = new CRCResponse(keymanager);

		// Enable required SSLProtocols
		System.setProperty("sslEnabledProtocols", "TLSv1.2,TLSv1.1,TLSv1");

		// Enable javax.net debug mode if the App is started in debug mode
		if (App.SERVER_PROTOCOL_DEBUG)
			System.setProperty("javax.net.debug", "all");
	}

	public void start(String password) throws IOException {
		// Passes keystore.jks which should be next to the jar to Nanohttpd
		makeSecure(NanoHTTPD.makeSSLSocketFactory("/keystore.jks", password.toCharArray()), null);

		// Start the internal server thread
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
	}

	@Override
	public Response serve(IHTTPSession session) {
		// check if the parameters contain a crc challenge parameter
		Map<String, String> parameters = session.getParms();
		Object crc = parameters.get(WebConstants.CRC_TOKEN_REQUEST_PARAMETER_KEY);
		if (crc != null) // is a crc challenge -> return the answer
			return doCRCCheck((String) crc);

		// If it was a post or put method get the content
		Map<String, String> files = new HashMap<String, String>();
		Method method = session.getMethod();
		if (Method.PUT.equals(method) || Method.POST.equals(method)) { // is put or put method
			try {
				session.parseBody(files);
			} catch (IOException | ResponseException ioe) {
				Logger.getInstance().handleError(ioe);
			}
		} // end post or put method

		// iterate through the content and add them to the queue to be processed
		for (Entry<String, String> entry : files.entrySet()) {
			jsonQueue.add(entry.getValue());
		}

		return SampleResponses.getOkay();
	}

	/**
	 * Response for the twitter crc challenge
	 */
	private Response doCRCCheck(String token) {
		if (!(token instanceof String)) {
			return SampleResponses.getInternalError();
		}

		String crcResponce = crcResponse.getChallengeResponse(token);

		if (crcResponce == null)
			return SampleResponses.getInternalError();

		JSONObject response = new JSONObject();
		response.put(WebConstants.CRC_TOKEN_RESPONSE_PARAMETER_KEY, crcResponce);

		return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT,
				response.toString());
	}

}
