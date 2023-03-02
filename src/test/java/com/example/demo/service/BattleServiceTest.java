package com.example.demo.service;

import static org.mockito.Mockito.*;

import java.security.Principal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Post;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;

@DataJpaTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BattleServiceTest {
	@Autowired
	BattleService battleService;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	PostRepository postRepository;
	@Mock
	Principal memberPrincipal;

	@BeforeAll
	void addDummyData() {
		Member firstMember = createMember();
		Member secondMember = createMember();
		memberRepository.save(firstMember);
		memberRepository.save(secondMember);

		P
	}

	@BeforeEach
	void setup() {

		when(memberPrincipal.getName()).thenReturn("1");
	}

	@Test
	void 실패_createBattle_배틀가능한_postId를_찾을_수_없음_EntityNotFoundException() {

	}

	@Test
	void 실패_createBattle_challengingPost가_현재Member소유가_아님_IllegalArgumentException() {
	}

	@Test
	void 실패_reateBattle_challengedPost가_현재Member소유임_IllegalArgumentException() {
	}

	@Test
	void 실패_createBattle_두포스트의_genre가_다름_IllegalArgumentException() {

	}

	@Test
	void 실패_createBattle_사용자의_대결권이_0개임_IllegalStateExceiptino() {

	}

	@Test
	void 성공_createBattle_배틀을생성하는데_성공한다_Member의_countOfChallengeTicket감소_배틀생성() {
		//given

		//when

		//then
	}

	private Post createPost(String musicId, Member member) {
		// PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
		// 	.musicId(musicId)
		// 	.musicName("musicName")
		// 	.musicUrl("musicUrl")
		// 	.albumCoverUrl("albumCoverUrl")
		// 	.genre(Genre.BALLAD)
		// 	.singer("singer")
		// 	.isBattlePossible("isPossibleBattle")
		// 	.content(content)
		// 	.build();
		// Post post = postCreateRequestDto.toEntity(member);
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
