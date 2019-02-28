package everyst.analytics.listner.parser;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import everyst.analytics.listner.twitter.Tweet;
import everyst.analytics.listner.twitter.TweetEngagement;
import everyst.analytics.listner.twitter.User;
import everyst.analytics.listner.twitter.events.BlockEvent;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.twitter.events.EventUtil;
import everyst.analytics.listner.twitter.events.FavoriteEvent;
import everyst.analytics.listner.twitter.events.FollowEvent;
import everyst.analytics.listner.twitter.events.MuteEvent;
import everyst.analytics.listner.twitter.events.TweetCreateEvent;
import everyst.analytics.listner.twitter.events.TweetDeleteEvent;
import everyst.analytics.listner.utility.JSONUtil;

public abstract class EventParser {

	public static void addAll(ArrayList<Event> events, String json) {
		JSONObject base = new JSONObject(json);

		JSONArray array = null;
		try {
			array = JSONUtil.getArray("favorite_events", base);
		} catch (JSONException e) {
		}
		if (array != null)
			for (int i = 0; i < array.length(); i++)
				events.add(new FavoriteEvent(json, array.getJSONObject(i)));
		
		array = null;
		try {
			array = JSONUtil.getArray("follow_events", base);
		} catch (JSONException e) {
		}

		if (array != null)
			for (int i = 0; i < array.length(); i++)
				events.add(new FollowEvent(json, array.getJSONObject(i)));
		
		array = null;
		try {
			array = JSONUtil.getArray("block_events", base);
			if (array != null)
				for (int i = 0; i < array.length(); i++)
					events.add(new BlockEvent(json, array.getJSONObject(i)));

		} catch (JSONException e) {
		}
		
		array = null;
		try {
			array = JSONUtil.getArray("mute_events", base);
		} catch (JSONException e) {
		}
		if (array != null)
			for (int i = 0; i < array.length(); i++)
				events.add(new MuteEvent(json, array.getJSONObject(i)));
		
		array = null;
		try {
			array = JSONUtil.getArray("tweet_delete_events", base);
		} catch (JSONException e) {
		}
		if (array != null)
			for (int i = 0; i < array.length(); i++)
				events.add(new TweetDeleteEvent(json, array.getJSONObject(i)));
		
		array = null;
		try {
			array = JSONUtil.getArray("tweet_create_events", base);
		} catch (JSONException e) {
		}
		if (array != null)
			for (int i = 0; i < array.length(); i++)
				events.add(new TweetCreateEvent(json, array.getJSONObject(i)));
		
		if (events.isEmpty())
			throw new JSONException("No events found!");
	}

	public static Tweet getTweetObject(JSONObject json) throws JSONException {
		TweetEngagement engagement = getTweetEngagementObject(json);
		if (engagement == null)
			return null;

		String id = json.getString("id_str");

		JSONObject userJson = json.getJSONObject("user");
		User user = getUserObject(userJson);
		if (user == null)
			return null;

		LocalDateTime createdAt = EventUtil.getTime(json, "created_at");
		
		String text = json.getString("text");
		return new Tweet(id, user, createdAt, text, engagement);
	}

	public static TweetEngagement getTweetEngagementObject(JSONObject json) throws JSONException {
		String quoteCount = Long.toString(json.getLong("quote_count"));
		String replyCount = Long.toString(json.getLong("reply_count"));
		String retweetCount = Long.toString(json.getLong("retweet_count"));
		String favoriteCount = Long.toString(json.getLong("favorite_count"));
		return new TweetEngagement(quoteCount, replyCount, retweetCount, favoriteCount);
	}

	public static User getUserObject(JSONObject json) throws JSONException {
		Object idObject = json.get("id");
		String id;
		if (idObject instanceof String)
			id = (String) idObject;
		else if (idObject instanceof Integer)
			id = Integer.toString((int) idObject);
		else if (idObject instanceof Long)
			id = Long.toString((long) idObject);
		else
			throw new JSONException("ID is something else: " + idObject.getClass().getName());
		
		String screenName = json.getString("screen_name");
		String followersCount = Long.toString(json.getLong("followers_count"));
		return new User(id, screenName, followersCount);
	}

	public static User[] getMentions(JSONArray array) {
		User[] mentions = new User[array.length()];
		for (int i = 0; i < array.length(); i++) {
			JSONObject mention = (JSONObject) array.get(i);
			String screenName = mention.getString("screen_name");
			String id = mention.getString("id_str");
			mentions[i] = new User(id, screenName, "0");
		}
		return mentions;
	}

}
