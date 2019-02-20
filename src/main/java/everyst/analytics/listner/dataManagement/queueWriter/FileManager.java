package everyst.analytics.listner.dataManagement.queueWriter;

import java.io.File;
import java.io.IOException;

import everyst.analytics.listner.dataManagement.Logger;

public class FileManager {

	/**
	 * The Structure of this File is [Type][old/new]. old is 0 and new is 1.
	 */
	private File[] files;
	/**
	 * How many times the file was stuff written to
	 */
	private int[] timesWrittenTo;
	private File root;

	public FileManager(File root) {
		this.root = root;
		files = FileManagerInit.init(root);

		timesWrittenTo = new int[files.length];
	}

	public File getFile(Type type) {
		if (timesWrittenTo[type.number]++ > FileConstants.QUEUE_WRITER_TIMES_UNTIL_FILE_NEEDS_CLEANING)
			moveFileToOld(type);
		return files[type.number];
	}

	/**
	 * Moves the current File to the old files
	 */
	public void moveFileToOld(Type type) {
		synchronized (files[type.number]) {
			// move the file to the other potential logs
			if (!FileManagerInit.moveFile(files[type.number], new File(root, type.path), false)) {
				Logger.getInstance().log("Could not move the newest File to the old ones. Ignoring the request!");
				return;
			}

			// create a new file for the objects to be written in
			try {
				files[type.number].createNewFile();
			} catch (IOException e) {
				Logger.getInstance().log("Fatal error. Could not create new file. Every try to log will now fail!");
				Logger.getInstance().handleError(e);
			}

			// Reset the clean number
			timesWrittenTo[type.number] = 0;
		}
	}

	/**
	 * Moves the current File to the old file and returns all old files
	 */
	public File[] getAllOldFiles(Type type) {
		// Move the newest file to the old ones first
		moveFileToOld(type);
		return new File(root, type.path).listFiles();
	}

}
