package com.example.demo.controller;

import java.security.Principal;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.ExceptionMessage;
import com.example.demo.common.ResponseMessage;
import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.dto.member.MemberBattlesResponseDto;
import com.example.demo.dto.member.MemberDetailsResponseDto;
import com.example.demo.dto.member.MemberMyDetailsResponseDto;
import com.example.demo.dto.member.MemberNicknameUpdateRequestDto;
import com.example.demo.dto.member.MemberUpdateResponseDto;
import com.example.demo.dto.ranking.RankersResponseDto;
import com.example.demo.exception.ServerNotExecuteException;
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

		throw new ServerNotExecuteException(ExceptionMessage.SERVER_ERROR.getMessage());
	}

	@PostMapping("/profile/nickname")
	public ResponseEntity<ApiResponse> updateNickname(
		Principal principal,
		@RequestBody MemberNicknameUpdateRequestDto requestDto
	) {

		MemberUpdateResponseDto response = memberService.updateNickname(principal, requestDto.nickname());
		return ResponseEntity.ok(
			ApiResponse.success(
				ResponseMessage.SUCCESS_USER_UPDATE.getMessage(),
				response
			)
		);
	}

	@PostMapping("/profile/image")
	public ResponseEntity<ApiResponse> updateProfileImage(
		Principal principal,
		@NotNull @RequestParam MultipartFile profileImage
	) {

		MemberUpdateResponseDto response = memberService.updateProfileImage(principal, profileImage);
		return ResponseEntity.ok(
			ApiResponse.success(
				ResponseMessage.SUCCESS_USER_UPDATE.getMessage(),
				response
			)
		);
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

	@GetMapping("/likes")
	public ResponseEntity<ApiResponse> getLikePosts(
		Principal principal,
		@RequestParam Optional<Genre> genre,
		@RequestParam Optional<Integer> limit) {

		MemberAllMyPostsResponseDto likePosts = memberService.getLikePosts(principal, genre, limit);
		return ResponseEntity.ok(
			ApiResponse.success(
				ResponseMessage.SUCCESS_USER_LIKE_POSTS.getMessage(),
				likePosts
			)
		);
  }
  
	@GetMapping("/ranking")
	public ResponseEntity<ApiResponse> getTop100Ranking() {
		RankersResponseDto rankersResponseDto = memberService.getRankerListFirstTo(100);
		ApiResponse apiResponse = ApiResponse.success(
			ResponseMessage.SUCCESS_FIND_RANKERS.getMessage(), rankersResponseDto
		);
		return ResponseEntity.ok(apiResponse);
	}

	@GetMapping("/ranking/preview")
	public ResponseEntity<ApiResponse> getRankingPreview() {
		RankersResponseDto rankersResponseDto = memberService.getRankerListFirstTo(5);
		ApiResponse apiResponse = ApiResponse.success(
			ResponseMessage.SUCCESS_FIND_RANKERS.getMessage(), rankersResponseDto
		);
		return ResponseEntity.ok(apiResponse);
	}
}
