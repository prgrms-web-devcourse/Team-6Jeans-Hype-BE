package com.example.demo.service.battle;

import java.security.Principal;

import org.springframework.stereotype.Service;

import com.example.demo.controller.battle.BattleCreateRequestDto;
import com.example.demo.model.member.Member;
import com.example.demo.service.PrincipalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleService {

	private PrincipalService principalService;

	public Long createBattle(Principal principal, BattleCreateRequestDto battleCreateRequestDto) {
		Member memberByPrincipal = principalService.getMemberByPrincipal(principal);
		return null;
	}
}
