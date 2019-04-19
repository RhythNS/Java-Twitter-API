package everyst.analytics.tasks.runnables.twitterFollowerTracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import everyst.analytics.listner.dataManagement.Logger;

public class DataManagement {

	private File currentIds, unFollowers, newFollowers;
	private Reciever reciever;

	public DataManagement(Reciever reciever, File root, String accountName) {
		this.reciever = reciever;

		if (!root.exists())
			root.mkdirs();
		if (!root.isDirectory())
			throw new IllegalStateException("Root is not a directory!");

		currentIds = new File(root, accountName + "CurrentIds.txt");
		unFollowers = new File(root, accountName + "Unfollowers.txt");
		newFollowers = new File(root, accountName + "Newfollowers.txt");
		if (!fileCheck(currentIds) || !fileCheck(unFollowers) || !fileCheck(newFollowers))
			System.exit(-1);

		if (unFollowers.length() < 2)
			try {
				saveString(unFollowers, "Time\tID\tScreenName\tFollowing\tFollowers\tlocation" + System.lineSeparator(),
						false);
			} catch (IOException e) {
				System.err.println("Could not write to unfollowers!");
				Logger.getInstance().handleError(e);
				System.exit(-1);
			}
	}

	/**
	 * Checks if a file exists. If not creates it
	 * 
	 * @param file - the file to be checked
	 * @return success - if an error occured or not
	 */
	private boolean fileCheck(File file) {
		try {
			if (!file.exists())
				file.createNewFile();
		} catch (IOException e) {
			Logger.getInstance().log("Could not create (" + file.getName() + "). Shutting down!");
			Logger.getInstance().handleError(e);
			return false;
		}
		return true;
	}

	/**
	 * Saves the new ids to file and checks if there were any un-/followers
	 * 
	 * @param newIds - the ids gotten from the twitter api
	 * @return success
	 * @throws InterruptedException
	 */
	public boolean recieveIds(ArrayList<Long> newIds) throws InterruptedException {
		ArrayList<Long> oldIds = null;

		// First load the old ids into ram
		try {
			oldIds = readIDs();
		} catch (IOException e) {
			Logger.getInstance().handleError(e);
			return false;
		}

		// now save the new ids to file
		try {
			saveIDs(currentIds, newIds, false);
		} catch (IOException e) {
			Logger.getInstance().handleError(e);
			return false;
		}

		// if there were no old ids just skip the check
		if (oldIds.isEmpty())
			return true;

		// remove all same ids
		Comparator.removeAllSame(oldIds, newIds);

		// any that are left in the old ids are unfollowers
		// any that are left in the new ids are new followers
		try {
			String converted = reciever.convertTagsToInformation(oldIds);
			if (converted == null)
				saveIDs(unFollowers, oldIds, true);
			else
				saveString(unFollowers, converted, true);

			saveIDs(newFollowers, newIds, true);
		} catch (IOException e) {
			Logger.getInstance().handleError(e);
			return false;
		}

		// yay everything worked
		return true;
	}

	/**
	 * Saves all ids into a file. Structure is id{line-seperator}id... There can be
	 * empty lines
	 * 
	 * @param file   - the file to be written to
	 * @param ids    - the ids that need to be written
	 * @param append - true appends to file, false overwrites
	 */
	private void saveIDs(File file, ArrayList<Long> ids, boolean append) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
		for (int i = 0; i < ids.size(); i++) {
			bw.write(ids.get(i) + System.lineSeparator());
		}
		bw.close();
	}

	private void saveString(File file, String string, boolean append) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
		bw.write(string);
		bw.close();
	}

	/**
	 * Read all ids from currentids
	 * 
	 * @return all ids
	 */
	private ArrayList<Long> readIDs() throws IOException {
		ArrayList<Long> ids = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(currentIds));

		// for loop but for reading everything in the file
		for (String s = ""; s != null; s = br.readLine()) {
			if (s.isEmpty()) // if there was an empty line skip
				continue;
			ids.add(new Long(s));
		}
		br.close();

		return ids;
	}

}
