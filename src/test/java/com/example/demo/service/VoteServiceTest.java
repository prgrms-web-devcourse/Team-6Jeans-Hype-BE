package com.example.demo.service;

import static com.example.demo.controller.TestUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.battle.Battle;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Post;
import com.example.demo.repository.BattleRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.VoteRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class VoteServiceTest {
	@Autowired
	private BattleRepository battleRepository;
	@Autowired
	private VoteRepository voteRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	VoteService voteService;

	Post challengedPost;
	Battle battle;

	@BeforeEach
	void setUp() {
		Member member = memberRepository.save(createMember());
		Member challenger = memberRepository.save(createMember());

		challengedPost = postRepository.save(createPost(member, createMusic()));
		Post challengingPost = postRepository.save(createPost(challenger, createMusic()));

		battle = battleRepository.save(createProgressBattle(challengedPost, challengingPost));

	}

	@Test
	void 게시글_동시_좋아요100개_수행시_횟수만큼_증가한다() throws InterruptedException {
		// given
		int numberOfThreads = 100;
		List<Member> memberList = new ArrayList<>();
		IntStream.rangeClosed(1, numberOfThreads)
			.forEach(i -> memberList.add(memberRepository.save(createMember())));
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

		// when
		for (int i = 0; i < numberOfThreads; i++) {
			Member member = memberList.get(i);
			executorService.submit(() -> {
				try {
					voteService.voteBattle(member, battle.getId(), challengedPost.getId());
				} finally {
					countDownLatch.countDown();
				}
			});
		}
		countDownLatch.await();
		Optional<Battle> findBattle = battleRepository.findById(battle.getId());

		// then
		assertThat(findBattle.isPresent()).isTrue();
		assertThat(findBattle.get().getChallengedPost().getVoteCount()).isEqualTo(numberOfThreads);
	}
}
