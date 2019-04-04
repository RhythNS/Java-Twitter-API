package everyst.analytics.tasks.schedulers;

import java.time.LocalDateTime;

public class IntervalScheduler implements Scheduler {

	private long days, hours, minutes, seconds;

	public IntervalScheduler(long days, long hours, long minutes, long seconds) {
		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
	}

	@Override
	public LocalDateTime scheduleNextRun(LocalDateTime previousRun) {
		return LocalDateTime.now().plusDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
	}

}
