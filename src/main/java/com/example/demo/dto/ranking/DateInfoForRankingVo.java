package com.example.demo.dto.ranking;

import java.time.LocalDate;

import com.example.demo.constant.RankingConstant;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record DateInfoForRankingVo(LocalDate from, LocalDate to) {
	private static final int term = RankingConstant.RANKING_TERM;

	public static DateInfoForRankingVo getDateInfoForRankingFromNow() {
		LocalDate now = LocalDate.now();
		LocalDate fromDate = now.minusDays(term - 1);
		return DateInfoForRankingVo.builder()
			.from(fromDate)
			.to(now)
			.build();
	}
}
