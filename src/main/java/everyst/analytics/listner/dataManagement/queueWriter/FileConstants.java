package everyst.analytics.listner.dataManagement.queueWriter;

import java.io.File;

public interface FileConstants {

	public final static boolean DEBUG = true;

	public final static File ROOT_DATA_MANAGEMENT_FILE = initRoot("data");
	public final static File LOG_FILE = new File(ROOT_DATA_MANAGEMENT_FILE, "log.txt");
	public static final File KEY_FILE = new File(ROOT_DATA_MANAGEMENT_FILE, "keys");
	public static final File KEYSTORE_FILE = new File(ROOT_DATA_MANAGEMENT_FILE, "keystore.jks");
	public final static File QUEUE_WRITER_FILE = new File(ROOT_DATA_MANAGEMENT_FILE, "queueWriterFile");

	public final static String QUEUE_WRITER_LOST_AND_FOUND_FILE_STRING = "LostAndFound";
	public final static int QUEUE_BYTES_UNTIL_FILE_NEEDS_CLEANING = 10_000_000;

	public final static char QUEUE_LINE_SEPERATOR = (char) 4;
	public final static char QUEUE_TYPE_SEPERATOR = (char) 2;

	static File initRoot(String path) {
		File root = new File(path);
		root.mkdirs();
		return root;
	}

}
