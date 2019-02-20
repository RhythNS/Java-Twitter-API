package everyst.analytics.listner.dataManagement.queueWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import everyst.analytics.listner.dataManagement.Logger;

public class StringReader {

	private FileManager stringFileManager;
	private BlockingQueue<String> msqQueue;

	public StringReader(FileManager stringFileManager, BlockingQueue<String> msqQueue) {
		this.stringFileManager = stringFileManager;
		this.msqQueue = msqQueue;
	}

	/**
	 * Adds all Files from the file system back into the system
	 * 
	 * @param delay - The delay used so the system is not flooded
	 */
	public void addAllStrings(Type type, long delay) {
		File[] files = stringFileManager.getAllOldFiles(type);

		for (int i = 0; i < files.length; i++) {
			try {
				// If the file did not have anything in it simple delete it and continue
				if (files[i].length() == 0) {
					files[i].delete();
					continue;
				}

				BufferedReader br = new BufferedReader(new FileReader(files[i]));
				StringBuilder sb = new StringBuilder();

				// Read all strings from the file
				String s = br.readLine();
				while (s != null) {
					sb.append(s);
					s = br.readLine();
				}

				// Split all strings based on the Constants.QueueSeperator
				String[] strings = sb.toString().split(Character.toString(FileConstants.QUEUE_SEPERATOR));

				// Put all Strings into the Queue. Wait for (delay) so we don't flood the queue
				for (int j = 0; j < strings.length; j++) {
					msqQueue.put(strings[j]);
					Thread.sleep(delay);
				}

				// close the stream so we don't leak any files
				br.close();

			} catch (IOException | InterruptedException e) {
				Logger.getInstance().handleError(e);
			}
		}

	}

}
