package com.example.demo.controller.member;

import static com.example.demo.controller.TestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.common.ApiResponse;
import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.MemberService;
import com.example.demo.service.PrincipalService;

@ExtendWith(MockitoExtension.class)
class MemberAllPostsTest {

	@Mock
	private MemberRepository memberRepository;

	PrincipalService principalService;
	MemberService memberService;
	MemberController memberController;

	@BeforeEach
	void setUp() {
		principalService = new PrincipalService(memberRepository);
		memberService = new MemberService(memberRepository);
		memberController = new MemberController(principalService, memberService);
	}

	@Test
	public void 성공_유저가_공유한_모든_게시물들을_조회할_수_있다() {
		// given
		Member member = createMember();
		UserDetails userDetails = createValidPrincipal();
		TestAuthentication authentication = new TestAuthentication(userDetails);

		// when
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		ResponseEntity<ApiResponse> responseEntity = memberController.getMemberAllPosts(authentication);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody().success()).isTrue();
		assertThat(responseEntity.getBody().data()).isNotNull();
	}

	@Test
	public void 실패_존재하지_않는_유저는_세부정보를_조회할_수_없다() {
		// given
		Member member = createMember();
		UserDetails userDetails = createInvalidUserDetails();
		TestAuthentication authentication = new TestAuthentication(userDetails);

		// when
		when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

		// then
		assertThatThrownBy(() -> {
			memberController.getMemberAllPosts(authentication);
		})
			.isExactlyInstanceOf(EntityNotFoundException.class);
	}

	private UserDetails createValidPrincipal() {
		return User.builder()
			.username("1")
			.password("password")
			.roles("USER")
			.build();
	}

	private UserDetails createInvalidUserDetails() {
		return User.builder()
			.username("-1")
			.password("password")
			.roles("USER")
			.build();
	}
}
