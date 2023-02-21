package com.example.demo.model.battle;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.post.Post;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BattleTest {
	@Mock
	private Post challengingPost;
	@Mock
	private Post challengedPost;

	@Test
	public void 성공_Battle생성_Post가_성공적으로들어오면_Battle이만들어진다() {
		Battle battle = createBattle(challengingPost, challengedPost);

		assertThat(battle.getChallengedPost().getVoteCount()).isEqualTo(0);
		assertThat(battle.getChallengingPost().getVoteCount()).isEqualTo(0);

	}

	@Test
	public void 실패_Battle생성_Post가Null이면_IllegalArgumentException이_발생한다() {

		IllegalArgumentException e1 = Assert.assertThrows(IllegalArgumentException.class, () ->
			createBattle(null, challengedPost)
		);
		IllegalArgumentException e2 = Assert.assertThrows(IllegalArgumentException.class, () ->
			createBattle(challengingPost, null)
		);
		IllegalArgumentException e3 = Assert.assertThrows(IllegalArgumentException.class, () ->
			createBattle(null, null)
		);
		log.info(e1.getMessage());
	}

	@Test
	public void 성공_havePost_Battle이_특정Post를_가진다면_true를_반환한다() {

	}

	@Test
	public void 실패_havePost_Battle이_특정Post를_가지않는다면_false를_반환한다() {
	}

	private Battle createBattle(Post challengingPost, Post challengedPost) {
		return Battle.builder().challengedPost(challengedPost).challengingPost(challengingPost).build();
	}
}
