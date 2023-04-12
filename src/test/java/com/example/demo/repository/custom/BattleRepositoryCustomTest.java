package com.example.demo.repository.custom;

import static com.example.demo.controller.TestUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.demo.config.JpaAuditingConfig;
import com.example.demo.config.QueryDslConfig;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Music;
import com.example.demo.model.post.Post;
import com.example.demo.repository.BattleRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;

@DataJpaTest
@Import({QueryDslConfig.class, BattleRepositoryCustom.class, JpaAuditingConfig.class})
class BattleRepositoryCustomTest {

	private static final int BATTLE_CNT = 10;
	@Autowired
	BattleRepository battleRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	BattleRepositoryCustom battleRepositoryCustom;

	@BeforeEach
	void setup() {
		IntStream.rangeClosed(1, BATTLE_CNT)
			.forEach(i -> {
				Member member1 = createMember(String.valueOf(i));
				Member member2 = createMember(String.valueOf(i * BATTLE_CNT + i));
				memberRepository.save(member1);
				memberRepository.save(member2);

				Post challengedPost = createPost(member1,
					createMusic(String.valueOf(i)));
				Post challengingPost = createPost(member2,
					createMusic(String.valueOf(i * BATTLE_CNT + i)));
				postRepository.save(challengedPost);
				postRepository.save(challengingPost);

				battleRepository.save(createBattle(challengedPost, challengingPost));
			});
	}

	@Test
	void QueryDsl을_이용하여_특정_시점보다_이전에_생성된_진행중인_대결을_가져올_수_있다() {
		List<Battle> progressBattles = battleRepositoryCustom
			.findByStatusAndCreatedAtIsBefore(BattleStatus.PROGRESS, LocalDateTime.now().plusDays(1));
		assertThat(progressBattles.size()).isEqualTo(BATTLE_CNT);
		progressBattles
			.forEach(battle ->
				assertThat(battle.getStatus()).isEqualTo(BattleStatus.PROGRESS)
			);
	}

	@Test
	void QueryDsl을_이용하여_특정_시점_사이에_수정되고_끝난_대결을_가져올_수_있다() {
		modifyBattle();
		List<Battle> progressBattles = battleRepositoryCustom
			.findByStatusAndUpdatedAtBetween(BattleStatus.END, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
		assertThat(progressBattles.size()).isEqualTo(BATTLE_CNT);
		progressBattles
			.forEach(battle ->
				assertThat(battle.getStatus()).isEqualTo(BattleStatus.END)
			);
	}

	private void modifyBattle() {
		List<Battle> allBattles = battleRepository.findAll();
		allBattles.forEach(Battle::endBattle);
	}

	private Battle createBattle(Post challengedPost, Post challengingPost) {
		return new Battle(
			challengedPost.getMusic().getGenre(),
			BattleStatus.PROGRESS,
			challengedPost,
			challengingPost
		);
	}

	private Music createMusic(String id) {
		return new Music(id,
			"aUrl",
			"s",
			"t",
			Genre.BALLAD,
			"mUrl");
	}
}
