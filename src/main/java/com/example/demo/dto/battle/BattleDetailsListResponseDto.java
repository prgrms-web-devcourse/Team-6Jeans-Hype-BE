package com.example.demo.dto.battle;

import java.util.List;

import com.example.demo.model.battle.Battle;

public record BattleDetailsListResponseDto(
	List<BattleDetailsResponseDto> battles
) {
	public static BattleDetailsListResponseDto of(List<Battle> battles) {
		return new BattleDetailsListResponseDto(
			battles.stream()
				.map(BattleDetailsResponseDto::of)
				.toList()
		);
	}
}
