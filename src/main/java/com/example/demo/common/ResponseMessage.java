package com.example.demo.common;

import lombok.Getter;

@Getter
public enum ResponseMessage {
	// Post
	SUCCESS_CREATE_POST("음악 공유 게시글 등록 성공"),
	SUCCESS_FIND_ALL_POST("음악 공유 게시글 리스트 조회 성공"),
	SUCCESS_FIND_POST("음악 공유 게시글 상세 조회 성공"),
	SUCCESS_FIND_ALL_CANDIDATE_POST("대결곡 후보 게시글 리스트 조회 성공"),

	SUCCESS_FIND_ALL_BATTLE_DETAILS("대결 상세 정보 리스트 조회 성공"),

	SUCCESS_FIND_BATTLE_BY_MEMBER("유저 참여 대결 정보 리스트 조회 성공");

	final String message;

	ResponseMessage(String message) {
		this.message = message;
	}
}
