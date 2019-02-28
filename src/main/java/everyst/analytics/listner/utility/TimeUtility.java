package everyst.analytics.listner.utility;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtility {

	private static SimpleDateFormat timeFormat = new SimpleDateFormat("MM-dd>HH:mm:ss:");
	private static DateTimeFormatter fromAPIParser = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss Z yyyy");
	private static DateTimeFormatter toDatabaseParser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
			.withZone(ZoneId.of("GMT+9"));

	public static String getTime() {
		return timeFormat.format(new Date());
	}

	public static LocalDateTime parseFromApi(String time) throws DateTimeException {
		return LocalDateTime.from(fromAPIParser.parse(time));
	}

	public static String parseToDatabase(LocalDateTime time) {
		return toDatabaseParser.format(time);
	}

}
