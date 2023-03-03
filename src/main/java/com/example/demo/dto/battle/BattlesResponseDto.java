package com.example.demo.dto.battle;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.example.demo.model.battle.Battle;

import lombok.Builder;

@Builder
public record BattlesResponseDto(
	@NotNull List<BattleInfoVo> battles
) {
	public static BattlesResponseDto of(List<Battle> battles) {
		return BattlesResponseDto.builder().
			battles(battles.stream().map(BattleInfoVo::of).toList())
			.build();
	}
}
