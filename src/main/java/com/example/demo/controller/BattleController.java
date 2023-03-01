package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.battle.BattleDetailsListResponseDto;
import com.example.demo.service.BattleService;
import com.example.demo.service.PrincipalService;
import com.example.demo.service.VoteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/battles")
public class BattleController {

	private final PrincipalService principalService;
	private final BattleService battleService;
	private final VoteService voteService;

	@GetMapping
	public ResponseEntity<ApiResponse> getBattleDetailsList() {
		BattleDetailsListResponseDto responseDto = battleService.getBattleDetailsListInProgress();
		return ResponseEntity.ok(
			ApiResponse.success(
				"대결 상세 정보 리스트 조회 성공",
				responseDto
			)
		);
	}
}
