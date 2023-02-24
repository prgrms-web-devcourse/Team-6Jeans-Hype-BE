package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class PrincipalServiceTest {

	@InjectMocks
	private PrincipalService principalService;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private Principal principal;

	private final Member member = createMember();

	@Test
	void Principal의_memberId로_Member를_반환할_수_있다() {
		// given
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(principal.getName()).thenReturn(String.valueOf(0L));

		// when
		Member actual = principalService.getMemberByPrincipal(principal);

		// then
		assertThat(actual).isEqualTo(member);
		verify(memberRepository).findById(anyLong());
		verify(principal).getName();
	}

	private Member createMember() {
		return Member.builder()
			.profileImageUrl("profile")
			.nickname("name")
			.countOfChallengeTicket(5)
			.ranking(1)
			.victoryPoint(10)
			.victoryCount(10)
			.refreshToken("token")
			.socialType(Social.GOOGLE)
			.socialId("socialId")
			.build();
	}

}
