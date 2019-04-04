package everyst.analytics.listner.test;

import java.time.LocalDateTime;

import everyst.analytics.tasks.Task;
import everyst.analytics.tasks.TaskManager;
import everyst.analytics.tasks.schedulers.IntervalScheduler;
import everyst.analytics.tasks.schedulers.OnceADayScheduler;

public class TaskManagerTest {

	public static void main(String[] args) {
		TaskManager tm = new TaskManager(null);
		
		Runnable pingRunnable = new Runnable() {
			@Override
			public void run() {
				System.out.println("ping");
			}
		};
		
		Task pingTask = new Task("Ping", pingRunnable, new IntervalScheduler(0, 0, 0, 2));
		tm.addTask(pingTask);
		
		Runnable hiRunnable = new Runnable() {
			
			@Override
			public void run() {
				System.out.println("hi");
			}
		};
		
		Task hiTask = new Task("Hi", hiRunnable, new OnceADayScheduler(1, 17, 16, 20), LocalDateTime.now().minusDays(1));
		tm.addTask(hiTask);
		
		tm.start();
		
	}

}
