package everyst.analytics.listner.dataManagement.queueWriter;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.google.common.io.Files;

import everyst.analytics.listner.dataManagement.Logger;

public abstract class FileManagerInit {

	public static File[] init(File rootDir) {
		// Check if the rootDir of FileManager exists
		if (!rootDir.exists())
			rootDir.mkdir();

		// Create a type array to init all the needed files
		Type[] types = Type.values();
		File[] files = new File[types.length];

		// Check if type.path Directories exist. If not make them
		for (int i = 0; i < types.length; i++) {
			File dir = new File(rootDir, types[i].path);
			if (!dir.exists())
				dir.mkdirs();
		}

		// Check if LostAndFound exists. If not make it
		File lostAndFound = new File(rootDir, FileConstants.QUEUE_WRITER_LOST_AND_FOUND_FILE_STRING);
		if (!lostAndFound.exists())
			lostAndFound.mkdirs();

		checkForOldFiles(rootDir, types);

		// Iterate through the array to init all files
		for (int i = 0; i < files.length; i++) {
			files[i] = new File(rootDir, types[i].path + ".txt");
			try {
				files[i].createNewFile();
			} catch (IOException e) {
				Logger.getInstance().handleError(e);
				return null;
			}
		}
		return files;
	}

	/**
	 * Removes all old files from the directory and moves them to the type.path
	 */
	private static void checkForOldFiles(File rootDir, Type[] types) {
		// Get all files in the root directory
		String[] allFiles = rootDir.list();
		for (int i = 0; i < allFiles.length; i++) {
			File toHandle = new File(rootDir, allFiles[i]);

			// If that file is not a directory then it might needs to be moved
			if (!toHandle.isDirectory()) {
				boolean found = false;

				// Iterate through all Types
				for (int j = 0; j < types.length; j++) {

					// Found a file we might want
					if (toHandle.getName().contains(types[j].path)) {
						found = true;

						// Move the file to the type directory and give it a random number as a name
						if (moveFile(toHandle, new File(rootDir, types[j].path), false)) {
							break;
						}

						// Could not move the file. Something is wrong with the file system. Do a manual
						// check and then restart
						Logger.getInstance().log(
								"Something went wrong with cleaning up the old files. Please remove them manualy first");
						System.exit(-1);

					} // end if found a relevant file
				} // end for iteration through types

				// If the file was handles, continue with the next file
				if (found)
					continue;

				// No idea what it is. Move it to lost and found
				if (!moveFile(toHandle, new File(rootDir, FileConstants.QUEUE_WRITER_LOST_AND_FOUND_FILE_STRING),
						false)) {

					// Something is wrong with the file system. Do a manual check and then restart
					Logger.getInstance().log(
							"Something went wrong with cleaning up the old files. Please remove them manualy first");
					System.exit(-1);
				}

			} // end if the file is a directory
		} // end iterate through all files in the directory

	}

	public static boolean moveFile(File file, File directory, boolean onlyCopy) {
		File moveTo = new File(new Date().getTime() + "");
		if (!moveTo.exists()) {
			try {
				moveTo.createNewFile();
				if (onlyCopy)
					Files.copy(file, moveTo);
				else
					Files.move(file, moveTo);
			} catch (IOException e) {
				Logger.getInstance().handleError(e);
				return false;
			}
			return true;
		}
		return false;
	}
}
