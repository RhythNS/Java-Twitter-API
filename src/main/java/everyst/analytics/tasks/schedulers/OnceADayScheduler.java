package everyst.analytics.tasks.schedulers;

import java.time.LocalDateTime;

public class OnceADayScheduler implements Scheduler {

	private int atHour, atMinute, atSecond;
	private long daysAfterPreviousRun;

	public OnceADayScheduler(long daysAfterPreviousRun, int atHour, int atMinute, int atSecond) {
		this.atHour = atHour;
		this.atMinute = atMinute;
		this.atSecond = atSecond;
		this.daysAfterPreviousRun = daysAfterPreviousRun;
	}

	@Override
	public LocalDateTime scheduleNextRun(LocalDateTime previousRun) {
		return previousRun.plusDays(daysAfterPreviousRun).withHour(atHour).withMinute(atMinute).withSecond(atSecond);
	}

}
