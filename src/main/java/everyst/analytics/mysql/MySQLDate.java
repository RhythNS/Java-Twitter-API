package everyst.analytics.mysql;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MySQLDate {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String getCurrentTime() {
		return sdf.format(new Date());
	}

}
