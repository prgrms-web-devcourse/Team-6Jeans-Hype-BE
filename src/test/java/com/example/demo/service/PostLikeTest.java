package com.example.demo.service;

import static com.example.demo.controller.TestUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PostLikeTest {

	@Autowired
	MemberRepository memberRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	PostService postService;
	Member member;
	Post post;

	@BeforeEach
	void setUp() {
		member = memberRepository.save(createMember());
		post = postRepository.save(createPost(member));
	}

	@Test
	void 게시글_동시_좋아요100개_수행시_횟수만큼_증가한다() throws InterruptedException {
		// given
		int numberOfThreads = 100;
		List<Principal> principalList = new ArrayList<>();
		IntStream.rangeClosed(1, numberOfThreads)
			.forEach(i -> principalList.add(new TestAuthentication(
				new MemberDetails(String.valueOf(memberRepository.save(createMember()).getId())))));
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

		// when
		for (int i = 0; i < numberOfThreads; i++) {
			final int I = i;
			// postService.likePost(principalList.get(I), post.getId());
			executorService.submit(() -> {
				try {
					postService.likePost(principalList.get(I), post.getId());
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		// then
		countDownLatch.await();
		Post findPost = postRepository.findById(post.getId())
			.orElseThrow();

		assertThat(findPost.getLikeCount()).isEqualTo(numberOfThreads);
	}

	private Post createPost(Member member) {
		return Post.create(
			"musicId",
			"albumCoverUrl",
			"hype",
			"musicName",
			Genre.K_POP,
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
