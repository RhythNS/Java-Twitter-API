package everyst.analytics.listner.webhook;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD.Response;

public interface URLListner {

	public default boolean isPath(String path) {
		return getPath().equalsIgnoreCase(path);
	}

	public String getPath();

	public Response getResponse(Map<String, String> parameters);

}
