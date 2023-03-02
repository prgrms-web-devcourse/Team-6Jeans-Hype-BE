package com.example.demo.dto.battle;

import com.example.demo.dto.genre.GenreVoResponseDto;
import com.example.demo.model.battle.Battle;

import lombok.Builder;

@Builder
public record BattleDetailsResponseDto(
	Long battleId,
	GenreVoResponseDto battleGenre,
	BattlePostResponseVo challenged,
	BattlePostResponseVo challenging
) {

	public static BattleDetailsResponseDto of(Battle battle) {
		return BattleDetailsResponseDto.builder()
			.battleId(battle.getId())
			.battleGenre(GenreVoResponseDto.of(battle.getGenre()))
			.challenged(BattlePostResponseVo.of(battle.getChallengedPost().getPost()))
			.challenging(BattlePostResponseVo.of(battle.getChallengingPost().getPost()))
			.build();
	}
}
