package everyst.analytics.listner.parser;

import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.dataManagement.queueWriter.Type;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.utility.QueueWorker;
import everyst.analytics.mysql.MySQLConnection;

public class EventWorker extends QueueWorker<Event> {

	private MySQLConnection database;

	public EventWorker(BlockingQueue<Event> msqQueue, App app, MySQLConnection database, StringWriter writer,
			long delay) {
		super(msqQueue, app, writer, delay);
		this.database = database;
	}

	@Override
	protected void process(Event x) {
		try {
			synchronized (database) { // maybe not needed?
				x.doTransaction(database);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.getInstance().handleError(e);
			writeToFile(x, Type.ERROR);
		}
	}

	@Override
	protected void writeToFile(Event x, Type type) {
		writer.write(x.getData(), type);
	}

}
