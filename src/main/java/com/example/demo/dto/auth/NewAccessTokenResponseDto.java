package com.example.demo.dto.auth;

public record NewAccessTokenResponseDto(String newAccessToken) {
	public static NewAccessTokenResponseDto of(String newAccessToken) {
		return new NewAccessTokenResponseDto(newAccessToken);
	}
}
