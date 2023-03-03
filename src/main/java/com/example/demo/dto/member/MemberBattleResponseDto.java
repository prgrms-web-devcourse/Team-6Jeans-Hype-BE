package com.example.demo.dto.member;

import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;

import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder
public record MemberBattleResponseDto(
	@NonNull Long battleId,
	@NonNull MemberBattleGenreVO genre,
	@NonNull MemberBattlePostVO challenging,
	@NonNull MemberBattlePostVO challenged,
	@NonNull BattleStatus battleStatus
) {

	public static MemberBattleResponseDto of(Battle battle) {
		return MemberBattleResponseDto.builder()
			.battleId(battle.getId())
			.genre(MemberBattleGenreVO.of(battle.getGenre()))
			.challenging(MemberBattlePostVO.of(battle.getChallengingPost().getPost()))
			.challenged(MemberBattlePostVO.of(battle.getChallengedPost().getPost()))
			.battleStatus(battle.getStatus())
			.build();
	}
}
