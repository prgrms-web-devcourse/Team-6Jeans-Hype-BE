package com.example.demo.dto.auth;

public record AccessTokenResponseDto(String accessToken) {
	public static AccessTokenResponseDto of(String accessToken) {
		return new AccessTokenResponseDto(accessToken);
	}
}
