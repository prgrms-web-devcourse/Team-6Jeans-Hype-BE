package com.example.demo.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.vote.BattleVoteRequestDto;
import com.example.demo.dto.vote.VoteResultResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.service.PrincipalService;
import com.example.demo.service.VoteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/battles")
public class BattleController {
	private final PrincipalService principalService;
	private final VoteService voteService;

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
				"대결 투표 성공",
				voteResultDto
			)
		);
	}
}
