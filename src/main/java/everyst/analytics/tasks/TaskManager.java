package everyst.analytics.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.Logger;

public class TaskManager extends Thread {

	private ArrayList<Task> tasks;
	private App app;

	public TaskManager(App app) {
		tasks = new ArrayList<>();
		this.app = app;
	}

	/**
	 * Removes a Task from the TaskManger via .equals()
	 * 
	 * @param task - The task that should be removed
	 * @return If the task was found inside the list and was removed
	 */
	public boolean removeTask(Task task) {
		return tasks.remove(task);
	}

	/**
	 * Removes a Task from the TaskManager
	 * 
	 * @param name - The name of the Task
	 * @return If the task was found inside the list and was removed
	 */
	public boolean removeTask(String name) {
		for (int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).getName().equals(name)) {
				tasks.remove(i);
				return true;
			}
		}
		return false;
	}

	public void addTask(Task task) {
		tasks.add(task);
	}

	public String getTaskNames() {
		if (tasks.isEmpty())
			return "[]";
		StringBuilder sb = new StringBuilder("[").append(tasks.get(0).getName());
		for (int i = 1; i < tasks.size(); i++) {
			sb.append(", ").append(tasks.get(i).getName());
		}
		sb.append("]");
		return sb.toString();
	}

	public void forceExecuteTask(String name) {
		for (Task task : tasks) {
			if (task.getName().equals(name)) {
				new Thread(task).start();
			}
		}
	}

	@Override
	public void run() {
		while (!app.isExitRequested()) { // while the server does not want to close
			for (int i = 0; i < tasks.size(); i++) { // iterate through every Task
				// Get the current time
				LocalDateTime now = LocalDateTime.now();

				// if the task is finished and if the tasks next run is before the current time
				if (tasks.get(i).isFinished() && tasks.get(i).getNextRun().isBefore(now))
					new Thread(tasks.get(i)).start(); // start the task

			} // end iterate
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// if it is interrupted the server wants to shutdown. So we can ignore it
			} // end catch exception
		} // end while exit requested

		Logger.getInstance().log("TaskManager shut down!");
	}

}
