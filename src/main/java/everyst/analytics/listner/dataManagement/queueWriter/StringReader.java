package everyst.analytics.listner.dataManagement.queueWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import everyst.analytics.listner.dataManagement.Logger;

public class StringReader {

	private FileManager stringFileManager;
	private BlockingQueue<String> msqQueue;
	private StringWriter writer;

	public StringReader(FileManager stringFileManager, StringWriter writer, BlockingQueue<String> stringQueue) {
		this.stringFileManager = stringFileManager;
		this.msqQueue = stringQueue;
		this.writer = writer;
	}

	/**
	 * Adds all Files from the file system back into the system
	 * 
	 * @param delay - The delay used so the system is not flooded
	 */
	public void addAllStrings(Type type, long delay) {
		Map<String, Type> toWrite = new TreeMap<>();
		synchronized (stringFileManager) {
			ArrayList<File> files = stringFileManager.getAllOldFiles();

			for (int i = 0; i < files.size(); i++) {
				try {
					// If the file did not have anything in it delete it and continue
					if (files.get(i).length() == 0) {
						files.get(i).delete();
						continue;
					}

					BufferedReader br = new BufferedReader(new FileReader(files.get(i)));
					StringBuilder sb = new StringBuilder();

					// Read all strings from the file
					String s = br.readLine();
					while (s != null) {
						sb.append(s).append(System.lineSeparator());
						s = br.readLine();
					}

					// Split all strings based on the Constants.QueueSeperator
					String[] strings = sb.toString().split(FileConstants.QUEUE_LINE_SEPERATOR + "");

					// Put all Strings into the Queue. Wait for (delay) so we don't flood the queue
					for (int j = 0; j < strings.length; j++) {
						String[] finalString = strings[j].split(FileConstants.QUEUE_TYPE_SEPERATOR + "");

						if (finalString.length != 2) {
							Logger.getInstance()
									.log("FinalString in Stringreader has a weird length: " + finalString.length);
							toWrite.put(strings[j], Type.READ_ERROR);
							continue;
						}

						if (type != Type.ALL && !finalString[0].equals(String.valueOf(type.number))) {
							toWrite.put(finalString[1], Type.values()[type.number]);
							continue;
						}

						if (!msqQueue.offer(finalString[1], 2, TimeUnit.SECONDS)) {
							Logger.getInstance().log("Could not add String to StringReader. Already full!");
							return;
						}
							
						Thread.sleep(delay);
					}

					// close the stream
					br.close();

					files.get(i).delete();

				} catch (IOException | InterruptedException e) {
					Logger.getInstance().handleError(e);
				}
			}
		}
		for (Entry<String, Type> entry : toWrite.entrySet()) {
			writer.write(entry.getKey(), entry.getValue());
		}

	}

}
