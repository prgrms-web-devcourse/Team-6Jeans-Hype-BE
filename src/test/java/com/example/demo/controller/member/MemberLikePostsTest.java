package com.example.demo.controller.member;

import static com.example.demo.controller.TestUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.common.AmazonS3ResourceStorage;
import com.example.demo.common.ApiResponse;
import com.example.demo.common.ResourceStorage;
import com.example.demo.config.AwsS3Config;
import com.example.demo.controller.MemberController;
import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Like;
import com.example.demo.model.post.Music;
import com.example.demo.model.post.Post;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.MemberService;
import com.example.demo.service.PrincipalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@DataJpaTest
@Import(value = {AmazonS3ResourceStorage.class, AwsS3Config.class})
class MemberLikePostsTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	ResourceStorage resourceStorage;

	@Autowired
	LikeRepository likeRepository;

	MemberController memberController;

	PrincipalService principalService;

	MemberService memberService;

	private ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		principalService = new PrincipalService(memberRepository);
		memberService = new MemberService(principalService, postRepository, memberRepository, resourceStorage,
			likeRepository);
		memberController = new MemberController(principalService, memberService);
	}

	@AfterEach
	void clear() {
		likeRepository.deleteAll();
		postRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	void 성공_유저가_좋아요한_모든_게시글들을_조회할_수_있다() throws JsonProcessingException {
		// given
		int loop = 5;
		List<Post> expectedPosts = new ArrayList<>();
		Member liker = createMember();
		Member poster = createMember();
		MemberDetails userDetails = new MemberDetails(liker.getId().toString());
		Principal principal = new TestAuthentication(userDetails);

		for (int i = 0; i < loop; i++) {
			Music music = createMusic(
				String.format("ABCD%s", i),
				Genre.BALLAD
			);
			Post post = createPost(poster, music);
			createLike(post, liker);
			expectedPosts.add(post);
		}
		Collections.reverse(expectedPosts);

		// when
		ResponseEntity<ApiResponse> response = memberController.getLikePosts(principal, Optional.empty(),
			Optional.empty());
		String actualJson = mapper.writeValueAsString(response.getBody().data());
		MemberAllMyPostsResponseDto responseDto = mapper.readValue(actualJson, MemberAllMyPostsResponseDto.class);
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().success()).isTrue();
		assertThat(responseDto.myPosts()).hasSize(loop);
		assertThat(responseDto).usingRecursiveComparison()
			.isEqualTo(MemberAllMyPostsResponseDto.of(expectedPosts));
	}

	@Test
	void 성공_유저가_좋아요한_특정_개수의_게시글들을_조회할_수_있다() throws JsonProcessingException {
		// given
		int loop = 5;
		int expectedSize = 2;
		List<Post> expectedPosts = new ArrayList<>();
		Member liker = createMember();
		Member poster = createMember();
		MemberDetails userDetails = new MemberDetails(liker.getId().toString());
		Principal principal = new TestAuthentication(userDetails);

		for (int i = 0; i < loop; i++) {
			Music music = createMusic(
				String.format("ABCD%s", i),
				Genre.BALLAD
			);
			Post post = createPost(poster, music);
			createLike(post, liker);
			expectedPosts.add(post);
		}
		Collections.reverse(expectedPosts);

		// when
		ResponseEntity<ApiResponse> response = memberController.getLikePosts(principal, Optional.empty(),
			Optional.of(expectedSize));
		String actualJson = mapper.writeValueAsString(response.getBody().data());
		MemberAllMyPostsResponseDto responseDto = mapper.readValue(actualJson, MemberAllMyPostsResponseDto.class);
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().success()).isTrue();
		assertThat(responseDto.myPosts()).hasSize(expectedSize);
	}

	@Test
	void 성공_유저가_좋아요한_특정_장르의_게시글들을_조회할_수_있다() throws JsonProcessingException {
		// given
		int loop = 3;
		List<Post> expectedPosts = new ArrayList<>();
		Member liker = createMember();
		Member poster = createMember();
		MemberDetails userDetails = new MemberDetails(liker.getId().toString());
		Principal principal = new TestAuthentication(userDetails);

		for (int i = 0; i < loop; i++) {
			Music music = createMusic(
				String.format("ABCD%s", i),
				Genre.BALLAD
			);
			Post post = createPost(poster, music);
			createLike(post, liker);
		}

		for (int i = 0; i < loop; i++) {
			Music music = createMusic(
				String.format("ABCD%s", i),
				Genre.TROT
			);
			Post post = createPost(poster, music);
			createLike(post, liker);
			expectedPosts.add(post);
		}
		Collections.reverse(expectedPosts);

		// when
		ResponseEntity<ApiResponse> response = memberController.getLikePosts(principal, Optional.of(Genre.TROT),
			Optional.empty());
		String actualJson = mapper.writeValueAsString(response.getBody().data());
		MemberAllMyPostsResponseDto responseDto = mapper.readValue(actualJson, MemberAllMyPostsResponseDto.class);
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().success()).isTrue();
		assertThat(responseDto.myPosts()).hasSize(loop);
		assertThat(responseDto).usingRecursiveComparison()
			.isEqualTo(MemberAllMyPostsResponseDto.of(expectedPosts));
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

	private Like createLike(Post post, Member liker) {
		Like like = new Like(
			post,
			liker
		);
		likeRepository.save(like);
		return like;
	}
}
