package com.example.demo.dto.ranking;

import com.example.demo.model.member.Member;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record RankerInfoVo(
	Long memberId,
	String memberNickname,
	int memberRanking,
	int memberPoint
) {
	public static RankerInfoVo of(Member member) {
		return RankerInfoVo.builder()
			.memberId(member.getId())
			.memberNickname(member.getNickname())
			.memberRanking(member.getRanking())
			.memberPoint(member.getVictoryPoint())
			.build();
	}
}
