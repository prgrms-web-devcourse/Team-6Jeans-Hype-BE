package com.example.demo.dto.ranking;

import java.time.LocalDate;

import com.example.demo.constant.RankingConstant;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record DateInfoForRankingVo(LocalDate from, LocalDate to) {
	private static final int term = RankingConstant.RANKING_TERM;

	public static DateInfoForRankingVo getDateInfoForRankingFromNow() {
		LocalDate toDate = LocalDate.now().minusDays(1);
		LocalDate fromDate = toDate.minusDays(term);
		return DateInfoForRankingVo.builder()
			.from(fromDate)
			.to(toDate)
			.build();
	}
}
