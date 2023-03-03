package com.example.demo.dto.battle;

import javax.validation.constraints.NotNull;

import com.example.demo.dto.genre.GenreVoResponseDto;
import com.example.demo.dto.music.SimpleMusicInfoVo;
import com.example.demo.model.battle.Battle;

import lombok.Builder;

@Builder
public record BattleInfoVo(
	@NotNull Long battleId,
	@NotNull Boolean isProgress,
	@NotNull GenreVoResponseDto genre,
	@NotNull SimpleMusicInfoVo challenging,
	@NotNull SimpleMusicInfoVo challenged
) {
	public static BattleInfoVo of(Battle battle) {
		return BattleInfoVo.builder()
			.battleId(battle.getId())
			.isProgress(battle.isProgress())
			.genre(GenreVoResponseDto.of(battle.getGenre()))
			.challenging(SimpleMusicInfoVo.of(battle.getChallengingPost().getPost().getMusic()))
			.challenged(SimpleMusicInfoVo.of(battle.getChallengedPost().getPost().getMusic()))
			.build();
	}
}
