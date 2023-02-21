package com.example.demo.common;

public enum ExceptionMessage {
	// EntityNotFoundException
	NOT_FOUND_POST("존재하지 않는 게시글입니다."),
	NOT_FOUND_MEMBER("존재하지 않는 유저입니다.");

	final String message;

	ExceptionMessage(String message) {
		this.message = message;
	}
}
