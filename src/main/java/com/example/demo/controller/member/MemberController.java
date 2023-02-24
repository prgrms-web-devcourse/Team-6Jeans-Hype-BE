package com.example.demo.controller.member;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import javax.persistence.EntityNotFoundException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.dto.member.MemberPostVoResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.service.MemberService;
import com.example.demo.service.PrincipalService;
import com.example.demo.common.ExceptionMessage;
import com.example.demo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

	private final PrincipalService principalService;
	private final MemberService memberService;

	@GetMapping("/posts")
	public ResponseEntity<ApiResponse> getMemberAllPosts(Principal principal) {
		Member member = principalService.getMemberByPrincipal(principal);
		MemberAllMyPostsResponseDto responseDto = memberService.getAllPosts(member);
		return ResponseEntity.ok(
			ApiResponse.success(
				"유저가 공유한 게시글 리스트 조회 성공",
				responseDto)
		);
	}
	private final MemberRepository memberRepository;

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse> getMemberProfile(Authentication authentication) {
		// TODO : 소셜 로그인 구현 시에 UserDetails를 어떻게 구성하는지에 따라 달라짐. -> 세준이랑 나중에 의논하기.
		Long memberId = getMemberLongId(authentication);
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_FOUND_MEMBER.getMessage()));
		MemberPostVoResponseDto memberDetailsInfo = MemberPostVoResponseDto.of(member);

		return ResponseEntity.ok(
			ApiResponse.success(
				"유저 상세 정보 조회 성공",
				memberDetailsInfo)
		);
	}

	private Long getMemberLongId(Authentication authentication) {
		UserDetails userDetails = (UserDetails)authentication.getPrincipal();
		return Long.parseLong(userDetails.getUsername());
	}
}
