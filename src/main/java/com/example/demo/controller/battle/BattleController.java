package com.example.demo.controller.battle;

import static com.example.demo.common.ResponseMessage.*;

import java.net.URI;
import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.common.ApiResponse;
import com.example.demo.service.battle.BattleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/battles")
@RequiredArgsConstructor
public class BattleController {
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
}
