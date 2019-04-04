package everyst.analytics.listner.parser;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.dataManagement.queueWriter.Type;
import everyst.analytics.listner.twitter.events.Event;
import everyst.analytics.listner.utility.QueueWorker;

public class StringWorker extends QueueWorker<String> {

	private BlockingQueue<Event> eventQueue;
	private ArrayList<Event> parsedEvents;

	public StringWorker(BlockingQueue<String> stringQueue, BlockingQueue<Event> eventQueue, App app,
			StringWriter writer, long delay) {
		super(stringQueue, app, writer, delay);
		this.eventQueue = eventQueue;
		parsedEvents = new ArrayList<>();
	}

	@Override
	protected void process(String json) {
		// Parse the json to an event
		try {
			EventParser.addAll(parsedEvents, json);	
		} catch (JSONException e) {
			Logger.getInstance().handleError(e);
			writeToFile(json, Type.ERROR);
			return;
		}

		// if the event is unknown write it to file to inspect it later
		while (!parsedEvents.isEmpty()) {
			Event parsedEvent = parsedEvents.remove(0);

			if (parsedEvent.isError()) // If the event was unknown or an error occurred
				writeToFile(json, Type.ERROR);

			else {// if the event is known try to enqueue it to the eventQueue

				try {
					// if the eventQueue is already full, save the json to file
					if (!eventQueue.offer(parsedEvent, 2, TimeUnit.SECONDS)) {
						Logger.getInstance().log("EventQueue is full!");
						writeToFile(json, Type.FULL);
					}

				} catch (InterruptedException e) {
					writeToFile(json, Type.EXIT);
				}

			}
		}
	}

	@Override
	public void writeToFile(String x, Type type) {
		writer.write(x, type);
	}


}
