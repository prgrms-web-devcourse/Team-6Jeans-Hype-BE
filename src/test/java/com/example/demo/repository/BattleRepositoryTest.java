package com.example.demo.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.JpaAuditingConfig;
import com.example.demo.model.BaseEntity;
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
	@Transactional
	void 대결_조회시_createdAt_기준으로_내림차순으로_정렬된다() {
		//given
		battleRepository.deleteAll();

		Battle battleIsEnded1 = createBattle(Genre.EDM);
		Battle battleIsEnded2 = createBattle(Genre.EDM);
		battleIsEnded1.quitBattle();
		battleIsEnded2.quitBattle();

		List<Battle> kpopBattles = getBattles(Genre.K_POP);
		List<Battle> balladBattles = getBattles(Genre.BALLAD);

		battleRepository.save(battleIsEnded1);
		battleRepository.save(battleIsEnded2);
		battleRepository.saveAll(kpopBattles);
		battleRepository.saveAll(balladBattles);

		List<Battle> allBattles = new ArrayList<>();
		allBattles.addAll(kpopBattles);
		allBattles.addAll(balladBattles);
		allBattles.add(battleIsEnded1);
		allBattles.add(battleIsEnded2);

		allBattles.sort(Comparator.comparing(BaseEntity::getCreatedAt).reversed());

		List<Battle> allProgressBattles = allBattles.stream().filter(Battle::isProgress).toList();

		List<Battle> allBalladBattlesCreatedAtDesc = allBattles.stream()
			.filter(element -> element.getGenre().equals(Genre.BALLAD)).toList();

		List<Battle> alledmAndEndBattles = allBattles.stream()
			.filter(element -> element.getStatus().equals(BattleStatus.END) && element.getGenre().equals(Genre.EDM))
			.toList();

		//when
		List<Battle> battlesByCreatedAtDesc = battleRepository.findAllByOrderByCreatedAtDesc();

		List<Battle> balladBattlesByCreatedAtDesc = battleRepository.findAllByGenreOrderByCreatedAtDesc(Genre.BALLAD);

		List<Battle> battlesByStatusProgressCreatedAtDesc = battleRepository.findAllByStatusOrderByCreatedAtDesc(
			BattleStatus.PROGRESS);

		List<Battle> battlesByEdmAndEndDesc = battleRepository
			.findAllByStatusAndGenreEqualsOrderByCreatedAtDesc(BattleStatus.END, Genre.EDM);

		//then
		for (int i = 0; i < battlesByCreatedAtDesc.size(); i++) {
			assertThat(battlesByCreatedAtDesc.get(i).getId()).isEqualTo(allBattles.get(i).getId());
			if (i + 1 < battlesByCreatedAtDesc.size()) {
				assertThat(
					battlesByCreatedAtDesc.get(i).getCreatedAt()
						.isAfter(battlesByCreatedAtDesc.get(i + 1).getCreatedAt())
				).isTrue();
			}
		}
		for (int i = 0; i < battlesByStatusProgressCreatedAtDesc.size(); i++) {
			assertThat(battlesByStatusProgressCreatedAtDesc.get(i).getId())
				.isEqualTo(allProgressBattles.get(i).getId());
			if (i + 1 < battlesByStatusProgressCreatedAtDesc.size()) {
				assertThat(
					battlesByStatusProgressCreatedAtDesc.get(i).getCreatedAt()
						.isAfter(battlesByStatusProgressCreatedAtDesc.get(i + 1).getCreatedAt())
				).isTrue();
			}
		}

		for (int i = 0; i < balladBattlesByCreatedAtDesc.size(); i++) {
			assertThat(balladBattlesByCreatedAtDesc.get(i).getId()).isEqualTo(
				allBalladBattlesCreatedAtDesc.get(i).getId());
			if (i + 1 < balladBattlesByCreatedAtDesc.size()) {
				assertThat(
					balladBattlesByCreatedAtDesc.get(i).getCreatedAt()
						.isAfter(balladBattlesByCreatedAtDesc.get(i + 1).getCreatedAt())
				).isTrue();
			}
		}

		for (int i = 0; i < battlesByEdmAndEndDesc.size(); i++) {
			assertThat(battlesByEdmAndEndDesc.get(i).getId()).isEqualTo(alledmAndEndBattles.get(i).getId());
			if (i + 1 < battlesByEdmAndEndDesc.size()) {
				assertThat(
					battlesByEdmAndEndDesc.get(i).getCreatedAt()
						.isAfter(battlesByEdmAndEndDesc.get(i + 1).getCreatedAt())
				).isTrue();
			}

		}

	}

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

	@Test
	@Transactional
	void battleStatus_challengingPost_challengedPost로_대결의_존재여부를_확인할_수_있다() {
		//given
		Battle battle = createBattle();
		Post challenginPost = battle.getChallengingPost().getPost();
		Post challengedPost = battle.getChallengedPost().getPost();
		battleRepository.save(battle);
		//when
		boolean shouldTrue = battleRepository.existsByChallengedPost_PostAndChallengingPost_PostAndStatus(
			challengedPost,
			challenginPost, status);
		boolean shouldFalse = battleRepository.existsByChallengedPost_PostAndChallengingPost_PostAndStatus(
			challenginPost,
			challengedPost, status);
		//than
		assertThat(shouldTrue).isTrue();
		assertThat(shouldFalse).isFalse();
	}

	private List<Battle> getBattles(Genre genre) {
		List<Battle> battles = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			battles.add(createBattle(genre));
		}
		return battles;
	}

	private Battle createBattle(Genre genre) {
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
