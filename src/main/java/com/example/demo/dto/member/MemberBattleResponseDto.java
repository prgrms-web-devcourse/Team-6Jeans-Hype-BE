package com.example.demo.dto.member;

import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;

import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder
public record MemberBattleResponseDto(
	@NonNull Long battleId,
	@NonNull MemberBattleGenre genre,
	@NonNull MemberBattlePost challenging,
	@NonNull MemberBattlePost challenged,
	@NonNull BattleStatus battleStatus
) {

	public static MemberBattleResponseDto of(Battle battle) {
		return MemberBattleResponseDto.builder()
			.battleId(battle.getId())
			.genre(MemberBattleGenre.of(battle.getGenre()))
			.challenging(MemberBattlePost.of(battle.getChallengingPost().getPost()))
			.challenged(MemberBattlePost.of(battle.getChallengedPost().getPost()))
			.battleStatus(battle.getStatus())
			.build();
	}
}
