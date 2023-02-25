package com.example.demo.security.oauth2;

import static com.example.demo.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.*;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.config.AppProperties;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.MemberService;
import com.example.demo.util.CookieUtils;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private TokenProvider tokenProvider;

	private AppProperties appProperties;

	private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	private final MemberService memberService;

	@Autowired
	OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider, AppProperties appProperties,
		HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
		MemberService memberService) {
		this.tokenProvider = tokenProvider;
		this.appProperties = appProperties;
		this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
		this.memberService = memberService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		long memberId = joinMember((OAuth2AuthenticationToken)authentication);

		String accessToken = tokenProvider.createAccessToken(memberId);
		String refreshToken = tokenProvider.createRefrshToken(memberId);
		memberService.assignRefreshToken(memberId, refreshToken);
		String targetUrl = determineTargetUrl(request, response, accessToken, refreshToken);
		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	private long joinMember(OAuth2AuthenticationToken authentication) {
		OAuth2AuthenticationToken oauth2Token = authentication;
		OAuth2User oAuth2User = oauth2Token.getPrincipal();
		String social = oauth2Token.getAuthorizedClientRegistrationId();
		return memberService.join(oAuth2User, social);
	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, String accessToken,
		String refreshToken) {
		Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
			.map(Cookie::getValue);

		if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			throw new RuntimeException(
				"Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication"
			);
		}

		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

		CookieUtils.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
		int refreshTokenExpirationMsec = (int)appProperties.getAuth().getRefreshTokenExpirationMsec();
		CookieUtils.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, refreshTokenExpirationMsec);
		return UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam("token", accessToken)
			.build().toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);

		return appProperties.getOauth2().getAuthorizedRedirectUris()
			.stream()
			.anyMatch(authorizedRedirectUri -> {
				// Only validate host and port. Let the clients use different paths if they want to
				URI authorizedUri = URI.create(authorizedRedirectUri);
				if (authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
					&& authorizedUri.getPort() == clientRedirectUri.getPort()) {
					return true;
				}
				return false;
			});
	}
}
