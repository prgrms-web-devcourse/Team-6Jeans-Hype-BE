package com.example.demo.controller;

import static com.example.demo.common.ResponseMessage.*;

import java.net.URI;
import java.security.Principal;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.ResponseMessage;
import com.example.demo.dto.battle.BattleCreateRequestDto;
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

	@PostMapping
	public ResponseEntity<ApiResponse> createBattle(
		Principal principal,
		@RequestBody BattleCreateRequestDto battleCreateRequestDto) {
		Long battleId = battleService.createBattle(principal, battleCreateRequestDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{battleId}")
			.buildAndExpand(battleId)
			.toUri();
		ApiResponse success = ApiResponse.success(SUCCESS_CREATE_BATTLE.getMessage());
		return ResponseEntity.created(location).body(success);
	}

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

	@GetMapping("/details")
	public ResponseEntity<ApiResponse> getBattleDetailsList(Principal principal) {
		BattleDetailsListResponseDto responseDto = battleService.getBattleDetailsListInProgress(principal);
		return ResponseEntity.ok(
			ApiResponse.success(
				ResponseMessage.SUCCESS_FIND_ALL_BATTLE_DETAILS.getMessage(),
				responseDto
			));
	}
}
