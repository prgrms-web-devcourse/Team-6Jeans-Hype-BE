package com.example.demo.dto.battle;

import com.example.demo.dto.genre.GenreVoResponseDto;
import com.example.demo.model.battle.Battle;

import lombok.Builder;

@Builder
public record BattleDetailsResponseDto(
	Long battleId,
	GenreVoResponseDto battleGenre,
	BattlePostResponseDto challenged,
	BattlePostResponseDto challenging
) {

	public static BattleDetailsResponseDto of(Battle battle) {
		return BattleDetailsResponseDto.builder()
			.battleId(battle.getId())
			.battleGenre(GenreVoResponseDto.of(battle.getGenre()))
			.challenged(BattlePostResponseDto.of(battle.getChallengedPost().getPost()))
			.challenging(BattlePostResponseDto.of(battle.getChallengingPost().getPost()))
			.build();
	}
}
