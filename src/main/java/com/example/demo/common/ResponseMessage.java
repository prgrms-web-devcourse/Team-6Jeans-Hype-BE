package com.example.demo.common;

import lombok.Getter;

@Getter
public enum ResponseMessage {
	// Member
	SUCCESS_FIND_BATTLE_BY_MEMBER("유저 참여 대결 정보 리스트 조회 성공"),
	SUCCESS_MY_PAGE_PROFILE("마이페이지 상세정보 조회 성공"),
	SUCCESS_USER_PAGE_PROFILE("유저페이지 상세정보 조회 성공"),
	// Post
	SUCCESS_CREATE_POST("음악 공유 게시글 등록 성공"),
	SUCCESS_FIND_ALL_POST("음악 공유 게시글 리스트 조회 성공"),
	SUCCESS_FIND_POST("음악 공유 게시글 상세 조회 성공"),
	// Battle
	SUCCESS_FIND_ALL_CANDIDATE_POST("대결곡 후보 게시글 리스트 조회 성공"),
	SUCCESS_FIND_ALL_BATTLE_DETAILS("대결 상세 정보 리스트 조회 성공"),
	SUCCESS_VOTE("대결 투표 성공"),
	SUCCESS_CREATE_BATTLE("배틀 등록 성공"),
	SUCCESS_FIND_BATTLES("대결 리스트 조회 성공");

	final String message;

	ResponseMessage(String message) {
		this.message = message;
	}
}
