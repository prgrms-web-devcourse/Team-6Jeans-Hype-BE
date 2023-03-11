package com.example.demo.controller;

import static com.example.demo.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.*;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.ExceptionMessage;
import com.example.demo.common.ResponseMessage;
import com.example.demo.dto.auth.AccessTokenResponseDto;
import com.example.demo.dto.auth.LoginCheckDto;
import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.AuthService;
import com.example.demo.util.CookieUtils;
import com.example.demo.util.TokenUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final TokenProvider tokenProvider;
	private final MemberRepository memberRepository;
	private final AuthService authService;

	@GetMapping("/refresh")
	public ResponseEntity<ApiResponse> tokenRefresh(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = TokenUtils.getAccessTokenFromRequest(request);
		if (Objects.isNull(accessToken)) {
			//액세스 토큰이 없습니다.
		}
		if (tokenProvider.isExpiredToken(accessToken)) {
			//accessToken이 만료됨
			String refreshToken = CookieUtils
				.getCookie(request, REFRESH_TOKEN_COOKIE_NAME)
				.map(Cookie::getName).orElse(null);
			if (tokenProvider.validateToken(refreshToken)) {
				//refreshToken이 유효함(accessToken 발급 가능)
				Optional<Member> memberByRefreshToken = memberRepository.findByRefreshToken(refreshToken);
				Long memberId = memberByRefreshToken.map(Member::getId).orElseThrow(()
					-> new EntityNotFoundException(ExceptionMessage.NOT_FOUND_MEMBER.getMessage())
				);
				if (tokenProvider.isWillExpiredInThreeDays(refreshToken)) {
					//refreshToken 만료까지 3일 남았다면 -> refreshToken 도 갱신해줌
					String newRefreshToken = tokenProvider.createRefrshToken(memberId);
					memberByRefreshToken.get().setRefreshToken(newRefreshToken);
					CookieUtils.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
					CookieUtils.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, newRefreshToken, 2592000);
				}
				String newAccessToken = tokenProvider.createAccessToken(memberId);
				ApiResponse apiResponse = ApiResponse.success(
					"새로운 accessToken 발급 성공",
					AccessTokenResponseDto.of(newAccessToken)
				);
				return ResponseEntity.ok().body(apiResponse);
			} else {
				//refreshToken이 유효하지 않음
				throw new IllegalArgumentException("InvalidRefreshToken : refresh token이 validate 하지 않습니다.");
			}
		} else {
			if (tokenProvider.validateToken(accessToken)) {
				throw new IllegalArgumentException("InvalidAccessToken : 액세스 토큰이 유효하지 않습니다.");
			} else {
				//accessToken이 만료되지 않음
				throw new IllegalArgumentException("ExpiredAccessToken : 액세스 토큰이 만료되지 않았습니다.");
			}
		}

	}

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
