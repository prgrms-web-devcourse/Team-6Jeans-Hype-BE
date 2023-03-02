package com.example.demo.common;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
	// EntityNotFoundException
	NOT_FOUND_POST("존재하지 않는 게시글입니다."),
	NOT_FOUND_MEMBER("존재하지 않는 유저입니다."),
	OBJECT_NOT_NULL("은(는) Null 일 수 없습니다."),

	// IllegalArgumentException
	DUPLICATED_USER_MUSIC_URL("이미 공유한 곡입니다."),
	SERVER_ERROR("서버 내부에 오류가 발생했습니다."),
	NOT_FOUND_BATTLE("존재하지 않는 대결입니다."),
	POST_NOT_CONTAIN_BATTLE("투표한 게시글이 대결에 존재하지 않습니다.");

	final String message;

	ExceptionMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
