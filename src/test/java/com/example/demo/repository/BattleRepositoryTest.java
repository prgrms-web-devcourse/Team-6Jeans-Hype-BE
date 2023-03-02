package com.example.demo.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.demo.config.JpaAuditingConfig;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;

@DataJpaTest
@Import(value = JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BattleRepositoryTest {

	@Autowired
	private BattleRepository battleRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PostRepository postRepository;

	private final BattleStatus status = BattleStatus.PROGRESS;
	private final Genre genre = Genre.K_POP;

	@Test
	void 상태와_특정날짜_전의_생성날짜를_기준으로_Battle을_반환할_수_있다() {
		// given
		List<Battle> mocks = getBattles();
		battleRepository.saveAll(mocks);
		LocalDateTime after = LocalDateTime.now().plusDays(3);

		for (Battle battle :
			mocks) {
			System.out.println(battle.getCreatedAt());
		}

		// when
		List<Battle> battles = battleRepository.findByStatusAndCreatedAtIsBefore(status, after);

		// then
		assertThat(battles.size()).isEqualTo(mocks.size());

		for (Battle battle : battles) {
			assertThat(battle.getStatus()).isEqualTo(status);
			assertThat(battle.getCreatedAt().isBefore(after)).isEqualTo(true);
		}
	}

	@Test
	void 상태와_특정날짜_사이의_수정날짜를_기준으로_Battle을_반환할_수_있다() {
		// given
		LocalDateTime before = LocalDateTime.now();
		List<Battle> mocks = getBattles();
		battleRepository.saveAll(mocks);
		LocalDateTime after = LocalDateTime.now();

		// when
		List<Battle> battles = battleRepository.findByStatusAndUpdatedAtBetween(status, before, after);

		// then
		assertThat(battles.size()).isEqualTo(mocks.size());

		for (Battle battle : battles) {
			assertThat(battle.getStatus()).isEqualTo(status);
			assertThat(battle.getUpdatedAt().isAfter(before)).isEqualTo(true);
			assertThat(battle.getUpdatedAt().isBefore(after)).isEqualTo(true);
		}
	}

	@Test
	void 대결ID로_대결을_조회할_수_있다() {
		Battle battle = createBattle();
		battleRepository.save(battle);

		Optional<Battle> result = battleRepository.findByIdPessimisticLock(battle.getId());

		assertThat(result).isPresent();
		assertThat(result.get())
			.usingRecursiveComparison()
			.isEqualTo(battle);
	}

	private List<Battle> getBattles() {
		List<Battle> battles = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			battles.add(createBattle());
		}
		return battles;
	}

	private Battle createBattle() {
		Member challengedMember = createMember();
		Member challengingMember = createMember();
		memberRepository.save(challengedMember);
		memberRepository.save(challengingMember);

		Post challengedPost = createPost(challengedMember);
		Post challengingPost = createPost(challengingMember);
		postRepository.save(challengedPost);
		postRepository.save(challengingPost);

		return Battle.builder()
			.genre(genre)
			.status(status)
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
