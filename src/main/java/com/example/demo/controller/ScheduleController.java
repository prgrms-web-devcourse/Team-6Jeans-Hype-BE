package com.example.demo.controller;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.BattleService;
import com.example.demo.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleController {

	private final BattleService battleService;
	private final MemberService memberService;

	private final int rankingTerm = 7;

	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void updateBattleResult() {
		battleService.quitBattles();
		memberService.resetAllRankingAndPoint();
		battleService.updateWinnerPoint(rankingTerm);
		memberService.updateAllMemberRanking();
	}
}
