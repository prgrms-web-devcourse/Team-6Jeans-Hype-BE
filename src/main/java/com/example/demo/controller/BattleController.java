package com.example.demo.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.ResponseMessage;
import com.example.demo.dto.battle.BattleDetailsListResponseDto;
import com.example.demo.dto.vote.BattleVoteRequestDto;
import com.example.demo.dto.vote.VoteResultResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.service.BattleService;
import com.example.demo.service.PrincipalService;
import com.example.demo.service.VoteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/battles")
public class BattleController {

	private final PrincipalService principalService;
	private final VoteService voteService;
	private final BattleService battleService;

	@PostMapping("/vote")
	public ResponseEntity<ApiResponse> vote(
		Principal principal,
		@Valid @RequestBody BattleVoteRequestDto requestDto
	) {
		Member member = principalService.getMemberByPrincipal(principal);
		Long battleId = requestDto.battleId();
		Long votedPostId = requestDto.votedPostId();

		VoteResultResponseDto voteResultDto = voteService.voteBattle(member, battleId, votedPostId);
		return ResponseEntity.ok(
			ApiResponse.success(
				ResponseMessage.SUCCESS_VOTE.getMessage(),
				voteResultDto
			));
	}

	@GetMapping
	public ResponseEntity<ApiResponse> getBattleDetailsList() {
		BattleDetailsListResponseDto responseDto = battleService.getBattleDetailsListInProgress();
		return ResponseEntity.ok(
			ApiResponse.success(
				ResponseMessage.SUCCESS_FIND_ALL_BATTLE_DETAILS.getMessage(),
				responseDto
			));
	}
}
