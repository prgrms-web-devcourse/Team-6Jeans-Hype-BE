package com.example.demo.dto.battle;

import javax.validation.constraints.NotNull;

import com.example.demo.dto.genre.GenreVoResponseDto;
import com.example.demo.dto.music.SimpleMusicInfoVo;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleInfo;

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

		BattleInfo challengingPostInfo = battle.getChallengingPost();
		BattleInfo challengedPostInfo = battle.getChallengedPost();
		int challengingVoteCount = challengingPostInfo.getVoteCount();
		int challengedVoteCount = challengedPostInfo.getVoteCount();
		boolean challengedIsWin = false;
		boolean challengingIsWin = false;

		if (!battle.isProgress()) {
			challengedIsWin = challengedVoteCount > challengingVoteCount;
			challengingIsWin = challengingVoteCount > challengedVoteCount;
		}
		return BattleInfoVo.builder()
			.battleId(battle.getId())
			.isProgress(battle.isProgress())
			.genre(GenreVoResponseDto.of(battle.getGenre()))
			.challenging(SimpleMusicInfoVo.of(challengingPostInfo.getPost().getMusic(), challengingIsWin))
			.challenged(SimpleMusicInfoVo.of(challengedPostInfo.getPost().getMusic(), challengedIsWin))
			.build();
	}
}
