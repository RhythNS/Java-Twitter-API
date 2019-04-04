package everyst.analytics.tasks.runnables;

import java.util.concurrent.BlockingQueue;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.dataManagement.queueWriter.Type;
import everyst.analytics.listner.utility.QueueWorker;

public abstract class WorkerCrashChecker<x> implements Runnable {

	protected App app;
	protected QueueWorker<x> worker;
	protected BlockingQueue<x> queue;

	public WorkerCrashChecker(App app, QueueWorker<x> worker, BlockingQueue<x> queue) {
		this.app = app;
		this.worker = worker;
		this.queue = queue;
	}

	@Override
	public void run() {
		if (!worker.isAlive() && !app.isExitRequested()) {
			Logger.getInstance().log("Worker " + worker.getClass().getSimpleName() + " has died. Restarting it...");
			// first create a replacement worker
			worker = createQueueWorker();

			// retrieve the json that made the other worker crash
			try {
				worker.writeToFile(queue.take(), Type.ERROR);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// set this worker as the active worker in the app
			setWorkerInApp(app);
			worker.start();
		}
	}

	protected abstract QueueWorker<x> createQueueWorker();

	protected abstract void setWorkerInApp(App app);

}
