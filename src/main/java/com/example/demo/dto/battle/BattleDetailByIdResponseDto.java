package com.example.demo.dto.battle;

import java.time.LocalDate;

import com.example.demo.dto.genre.GenreVoResponseDto;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleInfo;

import lombok.Builder;

@Builder
public record BattleDetailByIdResponseDto(
	Long battleId,
	boolean isProgress,
	Long selectedPostId,
	GenreVoResponseDto battleGenre,
	LocalDate battleCreatedDate,
	BattlePostInfoWithVoteCntResponseVo challenged,
	BattlePostInfoWithVoteCntResponseVo challenging
) {
	public static BattleDetailByIdResponseDto ofNotVoted(Battle battle) {
		BattleInfo challengedBattleInfo = battle.getChallengedPost();
		BattleInfo challengingBattleInfo = battle.getChallengingPost();
		if (battle.isProgress()) {
			return BattleDetailByIdResponseDto.builder()
				.battleId(battle.getId())
				.isProgress(true)
				.battleGenre(GenreVoResponseDto.of(battle.getGenre()))
				.battleCreatedDate(battle.getCreatedAt().toLocalDate())
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
				.battleGenre(GenreVoResponseDto.of(battle.getGenre()))
				.battleCreatedDate(battle.getCreatedAt().toLocalDate())
				.challenging(
					BattlePostInfoWithVoteCntResponseVo
						.ofWithVoteCnt(challengingBattleInfo.getPost(), challengingBattleInfo.getVoteCount()))
				.challenged(
					BattlePostInfoWithVoteCntResponseVo
						.ofWithVoteCnt(challengedBattleInfo.getPost(), challengedBattleInfo.getVoteCount()))
				.build();
		}
	}

	public static BattleDetailByIdResponseDto ofVoted(Battle battle, Long selectedPostId) {
		BattleInfo challengedBattleInfo = battle.getChallengedPost();
		BattleInfo challengingBattleInfo = battle.getChallengingPost();
		return BattleDetailByIdResponseDto.builder()
			.battleId(battle.getId())
			.isProgress(battle.isProgress())
			.selectedPostId(selectedPostId)
			.battleGenre(GenreVoResponseDto.of(battle.getGenre()))
			.battleCreatedDate(battle.getCreatedAt().toLocalDate())
			.challenging(
				BattlePostInfoWithVoteCntResponseVo
					.ofWithVoteCnt(challengingBattleInfo.getPost(), challengingBattleInfo.getVoteCount()))
			.challenged(
				BattlePostInfoWithVoteCntResponseVo
					.ofWithVoteCnt(challengedBattleInfo.getPost(), challengedBattleInfo.getVoteCount()))
			.build();

	}

}
