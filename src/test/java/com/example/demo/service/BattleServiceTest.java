package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.BattleRepository;
import com.example.demo.repository.custom.BattleRepositoryCustom;

@ExtendWith(MockitoExtension.class)
class BattleServiceTest {

	@InjectMocks
	private BattleService battleService;
	@Mock
	private BattleRepository battleRepository;

	@Mock
	private BattleRepositoryCustom battleRepositoryCustom;

	private final Genre genre = Genre.K_POP;
	private final BattleStatus progressStatus = BattleStatus.PROGRESS;
	private final Post challengedPost = createPost(createMember());
	private final Post challengingPost = createPost(createMember());

	@Test
	void 기간이_지난_대결을_종료할_수_있다() {
		// given
		List<Battle> battles = getBattles();

		// when
		when(battleRepositoryCustom.findByStatusAndCreatedAtIsBefore(any(), any())).thenReturn(battles);

		battleService.quitBattles();

		// then
		for (Battle battle : battles) {
			assertThat(battle.getStatus()).isEqualTo(BattleStatus.END);
		}

		verify(battleRepositoryCustom).findByStatusAndCreatedAtIsBefore(any(), any());
	}

	@Test
	void 승자의_승리_포인트를_업데이트할_수_있다() {
		// given
		List<Battle> battles = getBattles();
		for (Battle battle : battles) {
			battle.plusVoteCount(battle.getChallengedPost(), 10);
		}

		// when
		when(battleRepositoryCustom.findByStatusAndUpdatedAtBetween(any(), any(), any())).thenReturn(battles);

		battleService.updateWinnerPoint(7);

		// then
		for (Battle battle : battles) {
			assertThat(battle.getChallengedPost().getPost().getMember().getMemberScore().getVictoryPoint())
				.isEqualTo(10 * battles.size());
		}

		verify(battleRepositoryCustom).findByStatusAndUpdatedAtBetween(any(), any(), any());
	}

	private List<Battle> getBattles() {
		List<Battle> battles = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			battles.add(createBattle());
		}
		return battles;
	}

	private Battle createBattle() {
		return Battle.builder()
			.genre(genre)
			.status(progressStatus)
			.challengedPost(challengedPost)
			.challengingPost(challengingPost)
			.build();
	}

	private Post createPost(Member member) {
		return Post.create(
			"musicId",
			"albumCoverUrl",
			"hype",
			"musicName",
			genre,
			"musicUrl",
			"recommend",
			true,
			member
		);
	}

	private Member createMember() {
		return Member.builder()
			.profileImageUrl("profile")
			.nickname("name")
			.refreshToken("token")
			.socialType(Social.GOOGLE)
			.socialId("socialId")
			.build();
	}

}
