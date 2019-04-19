package everyst.analytics.tasks.runnables.twitterFollowerTracker;

import java.util.ArrayList;

public class Comparator {

	/**
	 * Removes all same values from two arraylists
	 * 
	 */
	public static void removeAllSame(ArrayList<Long> first, ArrayList<Long> second) {
		for (int i = first.size() - 1; i >= 0; i--) {
			int index = second.indexOf(first.get(i));

			if (index != -1) {
				first.remove(i);
				second.remove(index);
				i++;
				if (i > first.size()) // if it is out of bounds push it back to bounds
					i = first.size();
			}
		}

	}

}
