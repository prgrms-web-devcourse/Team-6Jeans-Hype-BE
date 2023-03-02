package com.example.demo.controller.member;

import static com.example.demo.controller.TestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.common.ApiResponse;
import com.example.demo.controller.MemberController;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.MemberService;
import com.example.demo.service.PrincipalService;

@ExtendWith(MockitoExtension.class)
class MemberAllPostsTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PostRepository postRepository;

	PrincipalService principalService;
	MemberService memberService;
	MemberController memberController;

	@BeforeEach
	void setUp() {
		principalService = new PrincipalService(memberRepository);
		memberService = new MemberService(principalService, postRepository, memberRepository);
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
		when(postRepository.findAllByIdLimitOrderByIdDesc(anyLong(), any(Pageable.class)))
			.thenReturn(List.of(
				Post.create(
					"ABCD1234",
					"albumCoverUrl",
					"singer",
					"title",
					Genre.CLASSIC,
					"musicUrl",
					"content",
					true,
					member),
				Post.create(
					"ABCD1235",
					"albumCoverUrl",
					"singer",
					"title",
					Genre.BALLAD,
					"musicUrl",
					"content",
					true,
					member)
			));
		ResponseEntity<ApiResponse> responseEntity = memberController
			.getMemberAllPosts(authentication, Optional.of(1L), Optional.empty(), Optional.of(2));

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
			memberController.getMemberAllPosts(authentication, Optional.of(1L), Optional.empty(), Optional.of(2));
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
