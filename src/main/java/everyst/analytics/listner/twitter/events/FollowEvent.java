package everyst.analytics.listner.twitter.events;

import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.parser.EventParser;
import everyst.analytics.listner.twitter.User;

public class FollowEvent extends Event {

	private User source, target;

	public FollowEvent(String data, JSONObject JSON) {
		super(data);

		JSONObject sourceJSON, targetJSON;
		try {
			sourceJSON = JSON.getJSONObject("source");
			targetJSON = JSON.getJSONObject("target");
		} catch (JSONException e) {
			Logger.getInstance().handleError(e);
			errorOccured();
			return;
		}

		source = EventParser.getUserObject(sourceJSON);
		if (source == null) {
			errorOccured();
			return;
		}

		target = EventParser.getUserObject(targetJSON);
		if (target == null)
			errorOccured();
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
