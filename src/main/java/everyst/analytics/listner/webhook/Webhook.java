package everyst.analytics.listner.webhook;

import java.io.IOException;
import java.util.ArrayList;
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
	private ArrayList<URLListner> listners;

	public Webhook(BlockingQueue<String> stringQueue, KeyManager keymanager) {
		// Init the Webhook with the Server Hostname and port
		super(WebConstants.HOSTNAME, WebConstants.PORT);

		this.jsonQueue = stringQueue;
		crcResponse = new CRCResponse(keymanager);
		listners = new ArrayList<>();

		// Enable required SSLProtocols
		System.setProperty("sslEnabledProtocols", "TLSv1.2,TLSv1.1,TLSv1");

		// Enable javax.net debug mode if the App is started in debug mode
		if (App.SERVER_PROTOCOL_DEBUG)
			System.setProperty("javax.net.debug", "all");
	}

	public void start(String password, boolean makeSecure) throws IOException {
		// Passes keystore.jks which should be next to the jar to Nanohttpd
		if (makeSecure)
			makeSecure(NanoHTTPD.makeSSLSocketFactory("/keystore.jks", password.toCharArray()), null);

		// Start the internal server thread
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
	}

	public boolean addListner(URLListner listner) {
		if (listner.getPath().length() == 0) {
			Logger.getInstance().log("Webhook Error: Can not add listner. Root is not allowed!");
		}

		// Check if the path is already in the listners ArrayList
		for (URLListner list : listners) {
			if (list.getPath().equals(listner.getPath())) {
				Logger.getInstance().log("Webhook Error: Can not add listner. Already listening to that path");
				return false;
			}
		}

		// If every error was checked add it to the list
		return listners.add(listner);
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

		// check if we are listing to the url and return another page if yes
		for (URLListner listner : listners) {
			if (listner.isPath(session.getUri()))
				return listner.getResponse(parameters, files);
		}

		// iterate through the content and add them to the queue to be processed
		for (Entry<String, String> entry : files.entrySet()) {
			jsonQueue.add(entry.getValue());
		}

		// if we did not run into any problems we are going to assume that we handled the process
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
