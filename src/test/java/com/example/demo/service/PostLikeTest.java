package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PostLikeTest {

	@Autowired
	MemberRepository memberRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	PostLockFacade postLockFacade;

	@Test
	void 성공_추천글_동시_좋아요_등록() throws InterruptedException {
		// given
		List<Member> members = new ArrayList<>();
		for (int i = 0; i < 50; i++) {
			members.add(memberRepository.save(createMember()));
		}

		Member member = memberRepository.save(createMember());
		Long postId = postRepository.save(createPost(member)).getId();

		// when
		ExecutorService executorService = Executors.newFixedThreadPool(50);
		CountDownLatch countDownLatch = new CountDownLatch(50);

		for (int i = 0; i < 50; i++) {
			int finalI = i;
			executorService.submit(() -> {
				try {
					postLockFacade.likePost(members.get(finalI), postId);
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		// then
		countDownLatch.await();
		Post post = postRepository.findById(postId)
			.orElseThrow();

		assertThat(post.getLikeCount()).isEqualTo(50);
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
