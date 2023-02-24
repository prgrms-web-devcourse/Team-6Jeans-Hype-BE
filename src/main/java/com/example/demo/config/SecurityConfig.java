package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.controller.UserService;
import com.example.demo.security.oauth2.OAuth2AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {
	private final UserService userService;

	public SecurityConfig(UserService userService) {
		this.userService = userService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.cors()
				.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.csrf()
			.disable()
			.formLogin()
			.disable()
			.httpBasic()
			.disable()
			.authorizeRequests()
			.antMatchers("/",
				"/error",
				"/favicon.ico",
				"/**/*.png",
				"/**/*.gif",
				"/**/*.svg",
				"/**/*.jpg",
				"/**/*.html",
				"/**/*.css",
				"/**/*.js")
			.permitAll()
			.antMatchers("/auth/**", "/oauth2/**")
			.permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.oauth2Login()
			.authorizationEndpoint()
			.baseUri("/oauth2/authorize") //로그인페이지를 받기위한 서버의 엔드포인트 설정
			// .authorizationRequestRepository(cookieOAuth2AuthorizationRequestRepository())
			.and()
			.redirectionEndpoint()
			.baseUri("/*/oauth2/code/*")
			.and()
			.successHandler(oAuth2AuthenticationSuccessHandler(userService));
		// return httpSecurity.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
		// 	.build();
		return httpSecurity.build();
	}

	@Bean
	public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(UserService userService) {
		return new OAuth2AuthenticationSuccessHandler(userService, tokenProvider);
	}
}
