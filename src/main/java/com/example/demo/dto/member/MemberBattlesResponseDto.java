package com.example.demo.dto.member;

import java.util.List;

import lombok.Builder;

@Builder
public record MemberBattlesResponseDto(
	List<MemberBattleResponseDto> battles
) {
	public static MemberBattlesResponseDto of(List<MemberBattleResponseDto> battles) {
		return MemberBattlesResponseDto.builder()
			.battles(battles)
			.build();
	}
}
