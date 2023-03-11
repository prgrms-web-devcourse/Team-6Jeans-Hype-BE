package com.example.demo.controller.battle;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.common.ApiResponse;
import com.example.demo.controller.BattleController;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Music;
import com.example.demo.model.post.Post;
import com.example.demo.repository.BattleRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.service.BattleService;
import com.example.demo.service.PrincipalService;
import com.example.demo.service.VoteService;

@DataJpaTest
class BattleRamdomTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	BattleRepository battleRepository;

	BattleService battleService;
	BattleController battleController;

	@BeforeEach
	void setup() {
		battleService = new BattleService(mock(PrincipalService.class), battleRepository, mock(VoteRepository.class),
			postRepository);
		battleController = new BattleController(mock(PrincipalService.class), mock(VoteService.class), battleService);
	}

	@Test
	void 성공_무작위로_진행중인_대결_상세정보를_조회할_수_있다() {
		// given
		Member member = createMember();
		Member challenger = createMember();
		createBattleList(member, challenger);

		// when
		ResponseEntity<ApiResponse> response = battleController.getRandomBattle();

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().data()).isNotNull();
	}

	private void createBattleList(Member member, Member challenger) {
		Genre genre = Genre.INDIE_ACOUSTIC;
		int loop = 5;
		for (int i = 0; i < loop; i++) {
			Post challengedPost = createPost(member, createMusic(String.format("ABCD12%s", i), genre));
			Post challengingPost = createPost(challenger, createMusic(String.format("ABCD17%s", i), genre));
			Battle battle = createBattle(genre, challengedPost, challengingPost);
		}
	}

	private Member createMember() {
		Member member = Member.builder()
			.nickname("nickname")
			.socialId("socialId")
			.refreshToken("refreshToken")
			.socialType(Social.GOOGLE)
			.profileImageUrl("profileImageUrl")
			.build();
		memberRepository.save(member);
		return member;
	}

	private Music createMusic(String musicId, Genre genre) {
		return new Music(
			musicId,
			"albumCoverUrl",
			"singer",
			"title",
			genre,
			"musicUrl"
		);
	}

	private Post createPost(Member member, Music music) {
		Post post = new Post(
			music,
			"content",
			true,
			0,
			member
		);
		postRepository.save(post);
		return post;
	}

	private Battle createBattle(Genre genre, Post challenged, Post challenging) {
		Battle battle = new Battle(
			genre,
			BattleStatus.PROGRESS,
			challenged,
			challenging
		);
		battleRepository.save(battle);
		return battle;
	}
}
