package com.example.demo.dto.member;

import java.util.Optional;

import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.post.Post;

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
		boolean challengingIsWin = false;
		boolean challengedIsWin = false;

		if (battle.getStatus().equals(BattleStatus.END)) {
			Optional<Post> wonPost = battle.getWonPost();
			if (wonPost.isPresent()) {
				if (wonPost.get().equals(battle.getChallengingPost().getPost())) {
					challengingIsWin = true;
				} else if (wonPost.get().equals(battle.getChallengedPost().getPost())) {
					challengedIsWin = true;
				}
			}
		}

		return MemberBattleResponseDto.builder()
			.battleId(battle.getId())
			.genre(MemberBattleGenreVO.of(battle.getGenre()))
			.challenging(MemberBattlePostVO.of(battle.getChallengingPost().getPost(), challengingIsWin))
			.challenged(MemberBattlePostVO.of(battle.getChallengedPost().getPost(), challengedIsWin))
			.battleStatus(battle.getStatus())
			.build();
	}
}
