package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableScheduling
public class ScheduleController {

	@Value("${spring.profiles.active}")
	private String profile;

	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void testScheduler() {
		System.out.println("schedule test - " + System.currentTimeMillis() / 10);
	}

	@Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
	public void testLogic() {
		if (profile.equals("test")) {
			System.out.println("schedule test - " + System.currentTimeMillis() / 10);
		}
	}
}
