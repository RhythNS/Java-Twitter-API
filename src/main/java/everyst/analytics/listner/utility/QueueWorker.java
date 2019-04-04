package everyst.analytics.listner.utility;

import java.util.concurrent.BlockingQueue;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.dataManagement.queueWriter.StringWriter;
import everyst.analytics.listner.dataManagement.queueWriter.Type;

public abstract class QueueWorker<x> extends Thread {

	protected BlockingQueue<x> queue;
	protected StringWriter writer;
	protected App app;

	public QueueWorker(BlockingQueue<x> msqQueue, App app, StringWriter writer, long delay) {
		this.queue = msqQueue;
		this.writer = writer;
		this.app = app;
	}

	@Override
	public void run() {
		try {
			while (!app.isExitRequested()) {
				try {
					if (queue.isEmpty())
						sleep(2000);
					else {
						// I would prefer to use take directly but if the thread crashes the object is
						// lost. That is why we peek and then take. We know something is in the queue
						// because we just checked if it is empty
						process(queue.peek());
						queue.take();
					}
				} catch (InterruptedException e) {
					// we only interrupt when we want to close the program. No need to log!
				}
			}

			// If the application is supposed to close and there are still objects left,
			// just write them to file
			while (!queue.isEmpty()) {
				try {
					writeToFile(queue.take(), Type.EXIT);
				} catch (InterruptedException e) {
					Logger.getInstance().handleError(e);
				}
			}
		} catch (Exception e) {
			// if anything unexcepted happened to our thread log it to file
			Logger.getInstance().handleError(e);
		}
		Logger.getInstance().log(this.getClass().getSimpleName() + " successfully shut down");
	}

	/**
	 * Called when an element gets taken from the queue and is meant to be processed
	 */
	protected abstract void process(x x);

	/**
	 * Called when an element is supposed to be written to the file system
	 */
	public abstract void writeToFile(x x, Type type);

}
