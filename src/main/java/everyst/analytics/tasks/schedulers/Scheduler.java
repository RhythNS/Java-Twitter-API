package everyst.analytics.tasks.schedulers;

import java.time.LocalDateTime;

public interface Scheduler {

	public LocalDateTime scheduleNextRun(LocalDateTime previousRun);
	
}
