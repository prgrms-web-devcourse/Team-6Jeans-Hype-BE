package com.example.demo.service;

import java.security.Principal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.auth.LoginCheckDto;
import com.example.demo.model.member.Member;
import com.example.demo.security.TokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
	private final PrincipalService principalService;
	private final TokenProvider tokenProvider;

	public LoginCheckDto checkLogin(Principal principal) {
		try {
			Member memberByPrincipal = principalService.getMemberByPrincipal(principal);
			return new LoginCheckDto(true);

		} catch (Exception e) {
			return new LoginCheckDto(false);
		}
	}

}
