package everyst.analytics.listner.dataManagement.queueWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import everyst.analytics.listner.dataManagement.Logger;

public class StringWriter {

	private FileManager stringFileManager;

	public StringWriter(FileManager stringFileManager) {
		this.stringFileManager = stringFileManager;
	}

	public void write(String string, Type type) {
		File file = stringFileManager.getFile();
		synchronized (file) {
			try {
				// Build the string
				StringBuilder sb = new StringBuilder();
				sb.append(type.number);
				sb.append(FileConstants.QUEUE_TYPE_SEPERATOR);
				sb.append(string);
				sb.append(FileConstants.QUEUE_LINE_SEPERATOR);
				
				// Write the string
				BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
				bw.write(sb.toString());
				bw.flush();
				bw.close();
			} catch (IOException e) {
				Logger.getInstance().handleError(e);
			}
		}
	}

}
