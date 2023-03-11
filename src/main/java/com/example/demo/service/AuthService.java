package com.example.demo.service;

import static com.example.demo.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.*;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.common.ExceptionMessage;
import com.example.demo.config.AppProperties;
import com.example.demo.dto.auth.LoginCheckDto;
import com.example.demo.dto.auth.NewAccessTokenResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.security.TokenProvider;
import com.example.demo.util.CookieUtils;
import com.example.demo.util.TokenUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
	private final PrincipalService principalService;
	private final TokenProvider tokenProvider;
	private final MemberRepository memberRepository;
	private final AppProperties appProperties;

	public NewAccessTokenResponseDto refreshAccessToken(HttpServletRequest request,
		HttpServletResponse response) {
		String accessToken = TokenUtils.getAccessTokenFromRequest(request);
		accessTokenNullCheck(accessToken);
		return getNewAccessTokenResponseDto(request, response, accessToken);
	}

	private static void accessTokenNullCheck(String accessToken) {
		if (Objects.isNull(accessToken)) {
			throw new IllegalArgumentException("InvalidAccessToken : 액세스 토큰이 유효하지 않습니다.");
			//액세스 토큰이 없습니다.
		}
	}

	private NewAccessTokenResponseDto getNewAccessTokenResponseDto(HttpServletRequest request,
		HttpServletResponse response, String accessToken) {
		if (tokenProvider.isOnlyExpiredToken(accessToken)) {
			//accessToken이 만료됨
			return getNewAccessTokenWhenAccesTokenIsExpired(request, response);
		} else {
			if (tokenProvider.validateToken(accessToken)) {
				throw new IllegalArgumentException("InvalidAccessToken : 액세스 토큰이 유효하지 않습니다.");
			} else {
				//accessToken이 만료되지 않음
				throw new IllegalArgumentException("NotExpiredAccessToken : 액세스 토큰이 만료되지 않았습니다.");
			}
		}
	}

	private NewAccessTokenResponseDto getNewAccessTokenWhenAccesTokenIsExpired(HttpServletRequest request,
		HttpServletResponse response) {
		String refreshToken = CookieUtils
			.getCookie(request, REFRESH_TOKEN_COOKIE_NAME)
			.map(Cookie::getName).orElse(null);
		if (tokenProvider.validateToken(refreshToken)) {
			//refreshToken이 유효함(accessToken 발급 가능)
			String newAccessToken = getNewAccessTokenWhenRefreshTokenIsValid(request, response, refreshToken);
			return NewAccessTokenResponseDto.of(newAccessToken);

		} else {
			//refreshToken이 유효하지 않음
			throw new IllegalArgumentException("InvalidRefreshToken : refresh token이 validate 하지 않습니다.");
		}
	}

	private String getNewAccessTokenWhenRefreshTokenIsValid(HttpServletRequest request, HttpServletResponse response,
		String refreshToken) {
		Optional<Member> memberByRefreshToken = memberRepository.findByRefreshToken(refreshToken);
		if (memberByRefreshToken.isEmpty()) {
			throw new EntityNotFoundException(ExceptionMessage.NOT_FOUND_MEMBER.getMessage());
		}
		Long memberId = memberByRefreshToken.get().getId();

		String newAccessToken = tokenProvider.createAccessToken(memberId);
		Optional<String> refreshTokenIfExpiredInThreeDays = getNewRefreshTokenIfExpiredInThreeDays(
			refreshToken, memberId);
		if (refreshTokenIfExpiredInThreeDays.isPresent()) {
			updateRefreshToken(request, response, refreshTokenIfExpiredInThreeDays, memberByRefreshToken.get());
		}
		return newAccessToken;
	}

	private void updateRefreshToken(HttpServletRequest request, HttpServletResponse response,
		Optional<String> refreshTokenIfExpiredInThreeDays, Member member) {
		if (refreshTokenIfExpiredInThreeDays.isPresent()) {
			member.setRefreshToken(refreshTokenIfExpiredInThreeDays.get());
			CookieUtils.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
			CookieUtils.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshTokenIfExpiredInThreeDays.get(),
				(int)appProperties.getAuth().getRefreshTokenExpirationMsec());
		}
	}

	private Optional<String> getNewRefreshTokenIfExpiredInThreeDays(String refreshToken
		, Long memberId) {
		if (tokenProvider.isWillExpiredInThreeDays(refreshToken)) {
			//refreshToken 만료까지 3일 남았다면 -> refreshToken 도 갱신해줌
			String newRefreshToken = tokenProvider.createRefrshToken(memberId);
			return Optional.of(newRefreshToken);
		} else {
			return Optional.empty();
		}
	}

	public LoginCheckDto checkLogin(Principal principal) {
		try {
			Member memberByPrincipal = principalService.getMemberByPrincipal(principal);
			return new LoginCheckDto(true);

		} catch (Exception e) {
			return new LoginCheckDto(false);
		}
	}

}
