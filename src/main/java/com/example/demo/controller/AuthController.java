package com.example.demo.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.ExceptionMessage;
import com.example.demo.common.ResponseMessage;
import com.example.demo.dto.auth.LoginCheckDto;
import com.example.demo.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@GetMapping("/login-check")
	public ResponseEntity<ApiResponse> checkLogin(Principal principal) {
		LoginCheckDto loginCheckDto = authService.checkLogin(principal);
		if (loginCheckDto.isLogin()) {
			ApiResponse success = ApiResponse
				.success(ResponseMessage.SUCCESS_AUTHORIZED_MEMBER.getMessage(), loginCheckDto);
			return ResponseEntity.ok(success);
		} else {
			ApiResponse fail = ApiResponse
				.fail(ExceptionMessage.UNAUTHORIZED_MEMBER.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(fail);
		}
	}
}
