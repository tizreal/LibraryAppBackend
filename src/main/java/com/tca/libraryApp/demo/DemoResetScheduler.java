package com.tca.libraryApp.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Triggers the demo reset on startup (the free host sleeps when idle, so most visits begin
 * with a fresh start) and on a schedule while the app stays awake. Failures are logged and
 * retried by the next trigger; the previous data stays intact.
 */
@Component
public class DemoResetScheduler {

	private static final Logger log = LoggerFactory.getLogger(DemoResetScheduler.class);

	private final DemoResetService resetService;

	public DemoResetScheduler(DemoResetService resetService) {
		this.resetService = resetService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onStartup() {
		run();
	}

	@Scheduled(cron = "${library.demo.reset-cron:0 0 */6 * * *}")
	public void onSchedule() {
		run();
	}

	private void run() {
		try {
			resetService.reset();
		} catch (RuntimeException e) {
			log.error("Demo reset failed; the previous data was kept: {}", e.getClass().getSimpleName());
		}
	}
}
