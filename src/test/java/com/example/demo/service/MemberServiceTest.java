package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;
	@Mock
	private MemberRepository memberRepository;

	@Test
	void 승리_포인트_기준으로_유저의_랭킹을_업데이트할_수_있다() {
		// given
		List<Member> members = getMembers();
		List<Integer> points = new ArrayList<>();
		for (int i = 0; i < members.size(); i++) {
			points.add((i + 1) * 10);
		}
		for (int i = 0; i < members.size(); i++) {
			members.get(i).plusPoint(points.get(i));
		}

		// when
		when(memberRepository.findAll()).thenReturn(members);
		when(memberRepository.findAll(Sort.by(Sort.Direction.DESC, "memberScore.victoryPoint")))
			.thenReturn(members);

		memberService.updateAllMemberRanking();

		// then
		for (Member member : members) {
			assertThat(member.getRanking())
				.isEqualTo(points.indexOf(member.getMemberScore().getVictoryPoint()) + 1);
		}

		verify(memberRepository).findAll();
		verify(memberRepository).findAll(Sort.by(Sort.Direction.DESC, "memberScore.victoryPoint"));
	}

	@Test
	void 모든_유저의_랭킹과_승리_포인트를_0으로_리셋할_수_있다() {
		// given
		List<Member> members = getMembers();
		for (int i = 0; i < members.size(); i++) {
			members.get(i).plusPoint((i + 1) * 10);
			members.get(i).updateRanking(i + 1);
		}

		// when
		when(memberRepository.findAll()).thenReturn(members);

		memberService.resetAllRankingAndPoint();

		// then
		for (Member member : members) {
			assertThat(member.getRanking()).isEqualTo(0);
			assertThat(member.getVictoryPoint()).isEqualTo(0);
		}

		verify(memberRepository).findAll();
	}

	@Test
	void 실패_유저에_null_토큰을_넣으면_에러가_발생한다() {
		// given
		Member member = createMember();

		// when
		when(memberRepository.findById(0L)).thenReturn(Optional.of(member));

		// then
		assertThatThrownBy(() -> memberService.assignRefreshToken(0L, null))
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	private List<Member> getMembers() {
		List<Member> members = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			members.add(createMember());
		}
		return members;
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
