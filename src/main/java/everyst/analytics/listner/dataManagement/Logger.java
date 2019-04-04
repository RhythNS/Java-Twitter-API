package everyst.analytics.listner.dataManagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.queueWriter.FileConstants;
import everyst.analytics.listner.utility.TimeUtility;
import everyst.analytics.telegram.TeleBot;

public class Logger {

	private static Logger instance;
	private TeleBot bot;

	public static Logger getInstance() {
		if (instance == null)
			instance = new Logger();
		return instance;
	}

	private BufferedWriter writer;

	/**
	 * Private constructor for Singleton Design Pattern
	 */
	private Logger() {
		try {
			// try to create a Log file if it does not exist yet
			File logFile = FileConstants.LOG_FILE;
			if (!logFile.exists())
				logFile.createNewFile();

			// Init the BufferedWriter with appending mode
			writer = new BufferedWriter(new FileWriter(logFile, true));
		} catch (IOException e) {
			System.err.println("Could not init Logger. All loging events will now be ignored!");
			e.printStackTrace();
			writer = null;
		}
	}
	
	public void setBot(TeleBot bot) {
		this.bot = bot;
	}

	/**
	 * Logs a given String to the log file. Only logs if initialization succeeded
	 * 
	 * @param toLog The String that should be logged
	 */
	public void log(String toLog) {
		// Check if the writer or the string is null
		System.out.println(toLog);
		if (writer != null && toLog != null && !toLog.isEmpty()) {
			
			// Create the string for logging
			StringBuilder sb = new StringBuilder();
			sb.append(TimeUtility.getTime());
			sb.append(toLog);
			sb.append('\n');

			// Try to log to file
			try {
				String lg = sb.toString();
				writer.write(lg);
				writer.flush();
				
				if (bot != null && !App.DEBUG)
					bot.sendMessage(lg);
				
			} catch (IOException e) {
				System.err.println("Logger could not log!");
				e.printStackTrace();
			} catch (TelegramApiException e) {
				System.err.println("Logger could not send message!");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Could not log: " + toLog);
		}
	}

	/**
	 * Prints error to System.err and to the log file
	 * 
	 * @param exception The thrown exception
	 */
	public void handleError(Exception exception) {
		exception.printStackTrace();
		if (exception != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(exception.getMessage());
			sb.append(Arrays.deepToString(exception.getStackTrace()));
			log(sb.toString());
		}
	}

}
