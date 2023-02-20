package com.example.demo.common;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record ApiResponse(
	int status,
	boolean success,
	@NonNull String message,
	Object data
) {

	public static ApiResponse success(int status, String message, Object data) {
		return ApiResponse.builder()
			.status(status)
			.success(true)
			.message(message)
			.data(data)
			.build();
	}

	public static ApiResponse fail(int status, String message) {
		return ApiResponse.builder()
			.status(status)
			.success(false)
			.message(message)
			.build();
	}
}
