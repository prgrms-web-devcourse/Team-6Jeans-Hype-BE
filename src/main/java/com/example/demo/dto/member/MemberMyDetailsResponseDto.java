package com.example.demo.dto.member;

import com.example.demo.model.member.Member;

import lombok.Builder;

@Builder
public record MemberMyDetailsResponseDto(
	String nickname,
	String profileImageUrl,
	int ranking,
	int victoryPoint,
	int victoryCount,
	int countOfChanllenge
) {
	public static MemberMyDetailsResponseDto of(Member member) {
		return MemberMyDetailsResponseDto.builder()
			.nickname(member.getNickname())
			.profileImageUrl(member.getProfileImageUrl())
			.ranking(member.getRanking())
			.victoryPoint(member.getVictoryPoint())
			.victoryCount(member.getVictoryCount())
			.countOfChanllenge(member.getCountOfChallengeTicket())
			.build();
	}
}
