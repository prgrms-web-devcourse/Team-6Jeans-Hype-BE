package com.example.demo.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.ExceptionMessage;
import com.example.demo.common.ResponseMessage;
import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.dto.member.MemberBattlesResponseDto;
import com.example.demo.dto.member.MemberDetailsResponseDto;
import com.example.demo.dto.member.MemberMyDetailsResponseDto;
import com.example.demo.dto.member.RankersResponseDto;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.service.MemberFilteringCase;
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
	public ResponseEntity<ApiResponse> getMemberAllPosts(
		Principal principal,
		@RequestParam Optional<Long> memberId,
		@RequestParam Optional<Genre> genre,
		@RequestParam Optional<Integer> limit) {

		MemberAllMyPostsResponseDto responseDto = memberService.getAllPosts(
			principal, memberId, genre, limit);
		return ResponseEntity.ok(
			ApiResponse.success(
				"유저가 공유한 게시글 리스트 조회 성공",
				responseDto)
		);
	}

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse> getMemberProfile(
		Principal principal,
		@RequestParam Optional<Long> memberId) {

		Member member = principalService.getMemberByPrincipal(principal);
		// TODO : 반환 Dto가 달라서 컨트롤러단에 분기를 둬야할듯 -> 없애려면 api 나누기, 다른 의견있다면 말해주삼.
		switch (MemberFilteringCase.getCase(memberId, member.getId())) {
			case MY_PAGE -> {
				return ResponseEntity.ok(
					ApiResponse.success(
						ResponseMessage.SUCCESS_MY_PAGE_PROFILE.getMessage(),
						MemberMyDetailsResponseDto.of(member))
				);
			}
			case USER_PAGE -> {
				return ResponseEntity.ok(
					ApiResponse.success(
						ResponseMessage.SUCCESS_USER_PAGE_PROFILE.getMessage(),
						MemberDetailsResponseDto.of(member))
				);
			}
		}

		throw new IllegalStateException(ExceptionMessage.SERVER_ERROR.getMessage());
	}

	@GetMapping("/battles")
	public ResponseEntity<ApiResponse> getBattles(Principal principal,
		@RequestParam(value = "memberId", required = false) Long memberId,
		@RequestParam(value = "battleStatus", required = false) BattleStatus battleStatus,
		@RequestParam(value = "genre", required = false) Genre genre,
		@RequestParam(value = "limit", required = false) Integer limit) {

		MemberBattlesResponseDto battles = memberService.getBattles(principal, memberId, battleStatus, genre, limit);

		ApiResponse apiResponse = ApiResponse.success(
			ResponseMessage.SUCCESS_FIND_BATTLE_BY_MEMBER.getMessage(), battles);

		return ResponseEntity.ok(apiResponse);
	}

	@GetMapping("/ranking")
	public ResponseEntity<ApiResponse> getRanking() {
		RankersResponseDto rankersResponseDto = memberService.getRankerList();
		ApiResponse apiResponse = ApiResponse.success(
			ResponseMessage.SUCCESS_FIND_POST.getMessage(), rankersResponseDto
		);
		return ResponseEntity.ok(apiResponse);
	}
}
