package com.example.demo.dto.battle;

import com.example.demo.dto.genre.GenreVoResponseDto;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleInfo;

import lombok.Builder;

@Builder
public record BattleDetailByIdResponseDto(
	Long battleId,
	boolean isProgress,
	boolean isVoted,
	GenreVoResponseDto battleGenre,
	BattlePostInfoWithVoteCntResponseVo challenged,
	BattlePostInfoWithVoteCntResponseVo challenging
) {
	public static BattleDetailByIdResponseDto ofVotedBattleDetail(Battle battle) {
		BattleInfo challengedBattleInfo = battle.getChallengedPost();
		BattleInfo challengingBattleInfo = battle.getChallengingPost();
		if (battle.isProgress()) {
			return BattleDetailByIdResponseDto.builder()
				.battleId(battle.getId())
				.isProgress(true)
				.isVoted(true)
				.battleGenre(GenreVoResponseDto.of(battle.getGenre()))
				.challenging(
					BattlePostInfoWithVoteCntResponseVo
						.ofWithoutVoteCnt(challengingBattleInfo.getPost()))
				.challenged(
					BattlePostInfoWithVoteCntResponseVo
						.ofWithoutVoteCnt(challengedBattleInfo.getPost()))
				.build();

		} else {
			return BattleDetailByIdResponseDto.builder()
				.battleId(battle.getId())
				.isProgress(false)
				.isVoted(true)
				.battleGenre(GenreVoResponseDto.of(battle.getGenre()))
				.challenging(
					BattlePostInfoWithVoteCntResponseVo
						.ofWithVoteCnt(challengingBattleInfo.getPost(), challengingBattleInfo.getVoteCount()))
				.challenged(
					BattlePostInfoWithVoteCntResponseVo
						.ofWithVoteCnt(challengedBattleInfo.getPost(), challengedBattleInfo.getVoteCount()))
				.build();
		}
	}

	public static BattleDetailByIdResponseDto ofNotVotedBattleDetail(Battle battle) {
		BattleInfo challengedBattleInfo = battle.getChallengedPost();
		BattleInfo challengingBattleInfo = battle.getChallengingPost();
		if (battle.isProgress()) {
			return BattleDetailByIdResponseDto.builder()
				.battleId(battle.getId())
				.isProgress(true)
				.isVoted(false)
				.battleGenre(GenreVoResponseDto.of(battle.getGenre()))
				.challenging(
					BattlePostInfoWithVoteCntResponseVo
						.ofWithoutVoteCnt(challengingBattleInfo.getPost()))
				.challenged(
					BattlePostInfoWithVoteCntResponseVo
						.ofWithoutVoteCnt(challengedBattleInfo.getPost()))
				.build();

		} else {
			return BattleDetailByIdResponseDto.builder()
				.battleId(battle.getId())
				.isProgress(false)
				.isVoted(false)
				.battleGenre(GenreVoResponseDto.of(battle.getGenre()))
				.challenging(
					BattlePostInfoWithVoteCntResponseVo
						.ofWithVoteCnt(challengingBattleInfo.getPost(), challengingBattleInfo.getVoteCount()))
				.challenged(
					BattlePostInfoWithVoteCntResponseVo
						.ofWithVoteCnt(challengedBattleInfo.getPost(), challengedBattleInfo.getVoteCount()))
				.build();
		}
	}
}
