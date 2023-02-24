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
import com.example.demo.dto.member.MemberDetailsResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberRepository memberRepository;

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse> getMemberProfile(Authentication authentication) {
		// TODO : 소셜 로그인 구현 시에 UserDetails를 어떻게 구성하는지에 따라 달라짐. -> 세준이랑 나중에 의논하기.
		Long memberId = getMemberLongId(authentication);
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_FOUND_MEMBER.getMessage()));
		MemberDetailsResponseDto memberDetailsInfo = MemberDetailsResponseDto.of(member);

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
