package com.example.demo.controller.member;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.dto.member.MemberDetailsResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.service.MemberService;
import com.example.demo.service.PrincipalService;

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

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse> getMemberProfile(Principal principal) {
		Member member = principalService.getMemberByPrincipal(principal);
		MemberDetailsResponseDto memberDetailsInfo = MemberDetailsResponseDto.of(member);

		return ResponseEntity.ok(
			ApiResponse.success(
				"유저 상세 정보 조회 성공",
				memberDetailsInfo)
		);
	}
}
