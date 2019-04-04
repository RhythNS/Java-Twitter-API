package everyst.analytics.tasks;

import java.time.LocalDateTime;

import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.tasks.schedulers.Scheduler;

public class Task implements Runnable {

	private boolean finished = true;
	private Runnable runnable;
	private Scheduler scheduler;
	private LocalDateTime nextRun;
	private String name;

	public Task(String name, Runnable runnable, Scheduler scheduler, LocalDateTime nextRun) {
		this.runnable = runnable;
		this.scheduler = scheduler;
		this.nextRun = nextRun;
		this.name = name;
	}

	/**
	 * Standard constructor with the next run being scheduled like it just ran
	 */
	public Task(String name, Runnable runnable, Scheduler scheduler) {
		this.name = name;
		this.runnable = runnable;
		this.scheduler = scheduler;
		this.nextRun = LocalDateTime.now();
		scheduleNextRun();
	}

	/**
	 * Executes a task
	 */
	@Override
	public void run() {
		if (!finished) { // checks if the task was called before the task finished before
			Logger.getInstance()
					.log("Can not start service " + runnable.getClass().getSimpleName() + "! Task was not finshed!");
			return;
		}
		
		// run the Task
		finished = false;
		runnable.run();
		finished = true;
		
		// schedule the next run
		scheduleNextRun();
	}

	public boolean isFinished() {
		return finished;
	}
	
	public LocalDateTime getNextRun() {
		return nextRun;
	}
	
	public String getName() {
		return name;
	}

	private void scheduleNextRun() {
		nextRun = scheduler.scheduleNextRun(nextRun);
	}

}
