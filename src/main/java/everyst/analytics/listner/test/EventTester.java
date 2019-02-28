package everyst.analytics.listner.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONException;

import everyst.analytics.listner.KeyManager;
import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.dataManagement.queueWriter.FileConstants;
import everyst.analytics.listner.parser.EventParser;
import everyst.analytics.listner.twitter.Tweet;
import everyst.analytics.listner.twitter.TweetEngagement;
import everyst.analytics.listner.twitter.User;
import everyst.analytics.listner.twitter.database.InitDatabase;
import everyst.analytics.listner.twitter.database.InputTwitterObjects;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.utility.TimeUtility;
import everyst.analytics.mysql.MySQLConnection;

public class EventTester {

	private static boolean READ_TEST = true, BASE_TEST = false, CREATE_TABLES = false;

	public static void main(String[] args) throws Exception {
		KeyManager keyManager = new KeyManager();
		keyManager.readKeys(FileConstants.KEY_FILE);
		MySQLConnection database = new MySQLConnection(keyManager.getDatabaseName(), keyManager.getDatabaseUser(),
				keyManager.getDatabasePassword());

		Scanner scan = new Scanner(System.in);

		if (CREATE_TABLES) {
			InitDatabase.init(database);
		}

		if (BASE_TEST) {
			User user = new User("2", "Rhyth", "1");
			InputTwitterObjects.inputUser(user, database);
			Tweet tweet = new Tweet("1", user, TimeUtility.parseFromApi("Thu Apr 06 15:24:15 +0000 2017"), "Test",
					new TweetEngagement("313", "153", "999999", "134"));
			InputTwitterObjects.inputTweet(tweet, database);

			System.exit(0);
		}

		if (READ_TEST) {
			BufferedReader read = new BufferedReader(
					new FileReader(new File(FileConstants.QUEUE_WRITER_FILE, "active")));
			String[] strings = read.readLine().split(FileConstants.QUEUE_LINE_SEPERATOR + "");

			ArrayList<Event> events = new ArrayList<>();
			for (int i = 0; i < strings.length; i++) {
				try {

					EventParser.addAll(events, strings[i].split(FileConstants.QUEUE_TYPE_SEPERATOR + "")[1]);
				} catch (JSONException e) {
					Logger.getInstance().handleError(e);
					System.out.println(strings[i].split(FileConstants.QUEUE_TYPE_SEPERATOR + "")[1]);
					System.out.println("-------------------------");
					System.out.println("Continue?");
					scan.nextLine();
				}
		
				while (!events.isEmpty()) {
					Event event = events.remove(0);
					try {
						synchronized (database) { // maybe not needed?
							event.doTransaction(database);
						}
					} catch (SQLException | JSONException e) {
						Logger.getInstance().handleError(e);
						System.out.println(event.getData());
						System.out.println("-------------------------");
						System.out.println("Continue?");
						scan.nextLine();
					}
				}

			}
			read.close();
			System.out.println("FUCK OFF!");
		}

		scan.close();
	}

}
