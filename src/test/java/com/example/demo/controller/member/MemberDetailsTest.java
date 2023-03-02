package com.example.demo.controller.member;

import static com.example.demo.controller.member.MemberTestUtil.*;
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
import com.example.demo.repository.PostRepository;
import com.example.demo.service.MemberService;
import com.example.demo.service.PrincipalService;

@ExtendWith(MockitoExtension.class)
public class MemberDetailsTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PostRepository postRepository;

	private PrincipalService principalService;
	private MemberService memberService;
	private MemberController memberController;

	@BeforeEach
	void setUp() {
		principalService = new PrincipalService(memberRepository);
		memberService = new MemberService(principalService, postRepository, memberRepository);
		memberController = new MemberController(principalService, memberService);
	}

	@Test
	public void 성공_유저_세부정보를_조회할_수_있다() {
		// given
		Member member = createMember();
		UserDetails userDetails = createValidUserDetails();
		TestAuthentication authentication = new TestAuthentication(userDetails);

		// when
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		ResponseEntity<ApiResponse> responseEntity = memberController.getMemberProfile(authentication);

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
		when(memberRepository.findById(any())).thenReturn(Optional.empty());

		// then
		assertThatThrownBy(() -> {
			memberController.getMemberProfile(authentication);
		})
			.isExactlyInstanceOf(EntityNotFoundException.class);
	}

	private UserDetails createValidUserDetails() {
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
