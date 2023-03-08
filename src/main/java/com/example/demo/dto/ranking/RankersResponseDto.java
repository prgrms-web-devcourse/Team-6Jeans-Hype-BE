package com.example.demo.dto.ranking;

import java.util.List;

import com.example.demo.model.member.Member;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record RankersResponseDto(
	DateInfoForRankingVo duration,
	List<RankerInfoVo> ranking) {
	public static RankersResponseDto of(List<Member> membersSortedByPoint) {
		return RankersResponseDto.builder()
			.duration(DateInfoForRankingVo.getDateInfoForRankingFromNow())
			.ranking(membersSortedByPoint.stream().map(RankerInfoVo::of).toList())
			.build();
	}

}
