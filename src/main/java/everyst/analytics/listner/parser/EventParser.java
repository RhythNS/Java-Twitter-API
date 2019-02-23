package everyst.analytics.listner.parser;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.twitter.Tweet;
import everyst.analytics.listner.twitter.TweetEngagement;
import everyst.analytics.listner.twitter.User;
import everyst.analytics.listner.twitter.events.BlockEvent;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.twitter.events.FavoriteEvent;
import everyst.analytics.listner.twitter.events.FollowEvent;
import everyst.analytics.listner.twitter.events.MuteEvent;
import everyst.analytics.listner.twitter.events.TweetCreateEvent;
import everyst.analytics.listner.twitter.events.TweetDeleteEvent;
import everyst.analytics.listner.utility.JSONUtil;
import everyst.analytics.listner.utility.TimeUtility;

public abstract class EventParser {

	public static void addAll(ArrayList<Event> events, String json) {
		JSONObject base = new JSONObject(json);

		JSONArray array = JSONUtil.getArray("favorited_status", base);
		if (array != null)
			for (int i = 0; i < array.length(); i++)
				events.add(new FavoriteEvent(json, base));

		array = JSONUtil.getArray("follow_events", base);
		if (array != null)
			for (int i = 0; i < array.length(); i++)
				events.add(new FollowEvent(json, base));

		array = JSONUtil.getArray("block_events", base);
		if (array != null)
			for (int i = 0; i < array.length(); i++)
				events.add(new BlockEvent(json, base));

		array = JSONUtil.getArray("mute_events", base);
		if (array != null)
			for (int i = 0; i < array.length(); i++)
				events.add(new MuteEvent(json, base));

		array = JSONUtil.getArray("tweet_delete_events", base);
		if (array != null)
			for (int i = 0; i < array.length(); i++)
				events.add(new TweetDeleteEvent(json, base));

		array = JSONUtil.getArray("tweet_create_events", base);
		if (array != null)
			for (int i = 0; i < array.length(); i++)
				events.add(new TweetCreateEvent(json, base));

	}

	public static Tweet getTweetObject(JSONObject json) {
		TweetEngagement engagement = getTweetEngagementObject(json);
		if (engagement == null)
			return null;

		String id = json.getString("id_str");
		if (id == null) {
			Logger.getInstance().log("Event Parser: Could not get id_str from tweet!");
			return null;
		}
		String createdAtString = json.getString("created_at");
		if (createdAtString == null) {
			Logger.getInstance().log("Event Parser: Could not get created_at from tweet!");
			return null;
		}
		LocalDateTime createdAt = null;
		try {
			createdAt = TimeUtility.parseFromApi(createdAtString);
		} catch (DateTimeException e) {
			Logger.getInstance().log("Event Parser: not Could parse created_at from tweet!");
			return null;
		}
		String text = json.getString("text");
		if (text == null) {
			Logger.getInstance().log("Event Parser: Could not get text from tweet!");
			return null;
		}
		return new Tweet(id, createdAt, text, engagement);
	}

	public static TweetEngagement getTweetEngagementObject(JSONObject json) {
		String impressions = json.getString("impressions");
		if (impressions == null) {
			Logger.getInstance().log("Event Parser: Could not get id_str from tweet engagement!");
			return null;
		}
		String engagements = json.getString("engagements");
		if (engagements == null) {
			Logger.getInstance().log("Event Parser: Could not get engagements from tweet engagement!");
			return null;
		}
		String quoteCount = json.getString("quote_count");
		if (quoteCount == null) {
			Logger.getInstance().log("Event Parser: Could not get quote_count from tweet engagement!");
			return null;
		}
		String replyCount = json.getString("reply_count");
		if (replyCount == null) {
			Logger.getInstance().log("Event Parser: Could not get reply_count from tweet engagement!");
			return null;
		}
		String retweetCount = json.getString("retweet_count");
		if (retweetCount == null) {
			Logger.getInstance().log("Event Parser: Could not get retweet_count from tweet engagement!");
			return null;
		}
		String favoriteCount = json.getString("favorite_count");
		if (favoriteCount == null) {
			Logger.getInstance().log("Event Parser: Could not get favorite_count from tweet engagement!");
			return null;
		}
		return new TweetEngagement(impressions, engagements, quoteCount, replyCount, retweetCount, favoriteCount);
	}

	public static User getUserObject(JSONObject json) {
		String id = json.getString("id_str");
		if (id == null) {
			Logger.getInstance().log("Event Parser: Could not get id_str from user!");
			return null;
		}
		String screenName = json.getString("screen_name");
		if (screenName == null) {
			Logger.getInstance().log("Event Parser: Could not get screenName from user!");
			return null;
		}
		String followersCount = json.getString("followers_count");
		if (followersCount == null) {
			Logger.getInstance().log("Event Parser: Could not get followers_count from user!");
			return null;
		}
		return new User(id, screenName, followersCount);
	}

}
