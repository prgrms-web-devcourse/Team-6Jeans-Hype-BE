package com.example.demo.controller.member;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.ExceptionMessage;
import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {
	private final MemberRepository memberRepository;

	@GetMapping("/posts")
	public ResponseEntity<ApiResponse> getMemberAllPosts(Authentication authentication) {
		Long memberId = getMemberLongId(authentication);
		Member member = memberRepository.findByIdFetchMember(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_FOUND_MEMBER.getMessage()));
		MemberAllMyPostsResponseDto responseDto = MemberAllMyPostsResponseDto.of(member);

		return ResponseEntity.ok(
			ApiResponse.success(
				"유저가 공유한 게시글 리스트 조회 성공",
				responseDto)
		);
	}

	private Long getMemberLongId(Authentication authentication) {
		UserDetails userDetails = (UserDetails)authentication.getPrincipal();
		return Long.parseLong(userDetails.getUsername());
	}
}
