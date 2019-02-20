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
		File file = stringFileManager.getFile(type);
		synchronized (file) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
				bw.write(string);
				bw.write(FileConstants.QUEUE_SEPERATOR);
				bw.flush();
				bw.close();
			} catch (IOException e) {
				Logger.getInstance().handleError(e);
			}
		}
	}

}
