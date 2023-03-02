package com.example.demo.controller.vote;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.vote.BattleVoteRequestDto;
import com.example.demo.dto.vote.VoteResultResponseDto;
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
class BattleVoteIntegrationTest {

	// TODO : 동시성 테스트
	// TODO : 예외에 대한 테스트 추가.
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	BattleRepository battleRepository;

	@Autowired
	VoteRepository voteRepository;

	@Autowired
	TokenProvider tokenProvider;

	@BeforeEach
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
	void 성공_진행중인_대결중_하나의_음악에_투표할_수_있다() throws JsonProcessingException {
		// given
		Member member = createMember();
		String accessToken = tokenProvider.createAccessToken(member.getId());
		Post selectedPost = createPost("ABCD1234", member);
		Post oppositePost = createPost("ABCD1235", member);
		Battle battle = createBattle(selectedPost, oppositePost);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("Bearer %s", accessToken));
		HttpEntity<BattleVoteRequestDto> requestBody = new HttpEntity<>(
			new BattleVoteRequestDto(battle.getId(), selectedPost.getId()), headers
		);
		VoteResultResponseDto expected = new VoteResultResponseDto(
			selectedPost.getMusic().getTitle(),
			selectedPost.getMusic().getAlbumCoverUrl(),
			1, 0);

		// when
		ResponseEntity<ApiResponse> response = restTemplate.withBasicAuth("1", "password")
			.postForEntity("/api/v1/battles/vote", requestBody, ApiResponse.class);
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().success()).isTrue();
		String actualJson = mapper.writeValueAsString(response.getBody().data());
		VoteResultResponseDto responseDto = mapper.readValue(actualJson, VoteResultResponseDto.class);
		assertThat(responseDto)
			.usingRecursiveComparison()
			.isEqualTo(expected);

		List<Vote> votes = voteRepository.findAll();
		assertThat(votes.size()).isEqualTo(1);
	}

	private Battle createBattle(Post post1, Post post2) {
		Battle battle = new Battle(
			post1.getMusic().getGenre(),
			BattleStatus.PROGRESS,
			post1,
			post2);
		battleRepository.save(battle);
		return battle;
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
}
