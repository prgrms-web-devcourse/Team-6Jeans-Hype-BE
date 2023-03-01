package com.example.demo.common;

import lombok.Getter;

@Getter
public enum ResponseMessage {
	// Post
	SUCCESS_CREATE_POST("음악 공유 게시글 등록 성공"),
	SUCCESS_FIND_ALL_POST("음악 공유 게시글 리스트 조회 성공"),
	SUCCESS_FIND_POST("음악 공유 게시글 상세 조회 성공"),
	SUCCESS_FIND_ALL_CANDIDATE_POST("대결곡 후보 게시글 리스트 조회 성공"),
	SUCCESS_CREATE_BATTLE("배틀 등록 성공");

	final String message;

	ResponseMessage(String message) {
		this.message = message;
	}
}
