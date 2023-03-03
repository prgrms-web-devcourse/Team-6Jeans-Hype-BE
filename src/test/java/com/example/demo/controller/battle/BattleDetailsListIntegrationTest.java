package com.example.demo.controller.battle;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.battle.BattleDetailsListResponseDto;
import com.example.demo.dto.vote.BattleVoteRequestDto;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.battle.Vote;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.BattleRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.security.TokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BattleDetailsListIntegrationTest {

	private static final int PROGRESS_SIZE = 5;
	private static final int ENDED_SIZE = 5;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private BattleRepository battleRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private VoteRepository voteRepository;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private ObjectMapper mapper = new ObjectMapper();

	@AfterEach
	void setUp() {
		clear();
	}

	private void clear() {
		voteRepository.deleteAll();
		battleRepository.deleteAll();
		postRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	void 성공_투표한_기록이_없는_진행중인_모든_배틀_정보를_조회할_수_있다() throws JsonProcessingException {
		// given
		Member member = createMember();
		String accessToken = tokenProvider.createAccessToken(member.getId());
		List<Battle> notVotedbattleListProgress = createProgressAndNotVotedBattleList(member);
		List<Battle> battleListEnd = createEndBattleList();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("Bearer %s", accessToken));
		HttpEntity<BattleVoteRequestDto> requestHeader = new HttpEntity<>(headers);
		BattleDetailsListResponseDto expected = BattleDetailsListResponseDto.of(notVotedbattleListProgress);

		// when
		ResponseEntity<ApiResponse> response = restTemplate.withBasicAuth("1", "password")
			.exchange("/api/v1/battles/details", HttpMethod.GET, requestHeader, ApiResponse.class);
		String actualJson = mapper.writeValueAsString(response.getBody().data());
		BattleDetailsListResponseDto responseDto = mapper.readValue(actualJson, BattleDetailsListResponseDto.class);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().success()).isTrue();
		assertThat(responseDto)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	private List<Battle> createProgressAndNotVotedBattleList(Member member) {
		List<Battle> battles = new ArrayList<>();
		for (int i = 0; i < PROGRESS_SIZE; i++) {
			Member member1 = createMember();
			Member member2 = createMember();
			Post post1 = createPost(String.format("ABCD123%s", i), member1);
			Post post2 = createPost(String.format("ABCD124%s", i), member2);
			Battle battle = createProgressBattle(post1, post2);
			if (i % 2 == 0) {
				createVote(member, post1, battle);
			} else {
				battles.add(battle);
			}
		}
		return battles;
	}

	private List<Battle> createEndBattleList() {
		List<Battle> battles = new ArrayList<>();
		for (int i = 0; i < ENDED_SIZE; i++) {
			Member member1 = createMember();
			Member member2 = createMember();
			Post post1 = createPost(String.format("ABCD125%s", i), member1);
			Post post2 = createPost(String.format("ABCD126%s", i), member2);
			Battle battle = createEndBattle(post1, post2);
			battles.add(battle);
		}
		return battles;
	}

	private Battle createProgressBattle(Post post1, Post post2) {
		Battle battle = new Battle(
			post1.getMusic().getGenre(),
			BattleStatus.PROGRESS,
			post1,
			post2);
		battleRepository.save(battle);
		return battle;
	}

	private Battle createEndBattle(Post post1, Post post2) {
		Battle battle = new Battle(
			post1.getMusic().getGenre(),
			BattleStatus.END,
			post1,
			post2);
		battleRepository.save(battle);
		return battle;
	}

	private Post createPost(String musicId, Member member) {
		Post post = Post.create(
			musicId,
			"albumCoverUrl",
			"singer",
			"title",
			Genre.CLASSIC,
			"musicUrl",
			"content",
			true,
			member
		);
		postRepository.save(post);
		return post;
	}

	private Member createMember() {
		Member member = new Member(
			"https://hype.music/images/1",
			"nickname",
			"refreshToken",
			Social.GOOGLE,
			"socialId");
		memberRepository.save(member);
		return member;
	}

	private Vote createVote(Member member, Post post, Battle battle) {
		Vote vote = new Vote(
			battle,
			post,
			member
		);
		voteRepository.save(vote);
		return vote;
	}
}
