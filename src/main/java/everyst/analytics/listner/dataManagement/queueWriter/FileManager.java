package everyst.analytics.listner.dataManagement.queueWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.google.common.io.Files;

import everyst.analytics.listner.dataManagement.Logger;

public class FileManager {

	/**
	 * How many times the file was stuff written to
	 */
	private File root, activeFile;
	private final String activeName = "active";

	public FileManager(File root) {
		this.root = root;
		root.mkdirs();

		activeFile = new File(root, activeName);
		if (activeFile.exists()) {
			moveActiveToOld();
			try {
				activeFile.createNewFile();
			} catch (IOException e) {
				Logger.getInstance().log("Could not create File for FileManager");
				Logger.getInstance().handleError(e);
				System.exit(0);
			}
		}
	}

	public File getFile() {
		synchronized (this) {
			if (activeFile.length() > FileConstants.QUEUE_BYTES_UNTIL_FILE_NEEDS_CLEANING) {
				moveActiveToOld();
				try {
					activeFile.createNewFile();
				} catch (IOException e) {
					Logger.getInstance().log("Could not create active File. Nothing is being logged anymore!");
					Logger.getInstance().handleError(e);
				}
			}
			return activeFile;
		}
	}

	/**
	 * Moves the current File to the old files
	 */
	public void moveActiveToOld() {
		try {
			// rename the active file to a name which is unique
			if (!activeFile.exists())
				return;
			Files.move(activeFile, new File(root, new Date().getTime() + ""));
			activeFile.createNewFile();
		} catch (IOException e) {
			Logger.getInstance().handleError(e);
		}
	}

	/**
	 * Moves the current File to the old file and returns all old files
	 */
	public ArrayList<File> getAllOldFiles() {
		synchronized (this) {
			// first move the current file to the old ones so we get everything in one go
			moveActiveToOld();

			File[] fileArray = root.listFiles();

			// iterate through the files and remove the current file
			ArrayList<File> files = new ArrayList<>();
			for (int i = 0; i < fileArray.length; i++) {
				if (!fileArray[i].getName().equals(activeName)) // if file is currentFile
					files.add(fileArray[i]);
			}
			return files;
		}
	}

}
