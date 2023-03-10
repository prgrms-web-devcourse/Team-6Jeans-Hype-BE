package com.example.demo.common;

import lombok.Getter;

@Getter
public enum ResponseMessage {
	// Member
	SUCCESS_FIND_BATTLE_BY_MEMBER("유저 참여 대결 정보 리스트 조회 성공"),
	SUCCESS_MY_PAGE_PROFILE("마이페이지 상세정보 조회 성공"),
	SUCCESS_USER_PAGE_PROFILE("유저페이지 상세정보 조회 성공"),
	SUCCESS_USER_UPDATE("유저 프로필 정보 수정 성공"),
	SUCCESS_USER_LIKE_POSTS("유저가 좋아요한 게시글 리스트 조회 성공"),
	SUCCESS_FIND_RANKERS("랭커 리스트 조회 성공"),
	// Post
	SUCCESS_CREATE_POST("음악 추천 게시글 등록 성공"),
	SUCCESS_FIND_ALL_POST("음악 추천 게시글 리스트 조회 성공"),
	SUCCESS_FIND_POST("음악 추천 게시글 상세 조회 성공"),
	SUCCESS_LIKE_POST("추천글 좋아요 성공"),
	SUCCESS_UNLIKE_POST("추천글 좋아요 해제 성공"),
	// Battle
	SUCCESS_FIND_ALL_CANDIDATE_POST("대결곡 후보 게시글 리스트 조회 성공"),
	SUCCESS_FIND_ALL_BATTLE_DETAILS("대결 상세 정보 리스트 조회 성공"),
	SUCCESS_VOTE("대결 투표 성공"),
	SUCCESS_CREATE_BATTLE("대결 등록 성공"),
	SUCCESS_FIND_BATTLES("대결 리스트 조회 성공"),
	SUCCESS_FIND_BATTLE_DETAIL_BY_ID("배틀 상세 조회 성공"),
	SUCCESS_RANDOM_BATTLE("랜덤한 대결 상세 조회 성공"),
	SUCCESS_GET_IS_LIKE("좋아요 여부 판단 성공"),
	//Auth
	SUCCESS_AUTHORIZED_MEMBER("인증 성공, 로그인된 사용자 입니다."),
	// Music
	SUCCESS_MUSIC_SEARCH("음악 검색 성공");

	final String message;

	ResponseMessage(String message) {
		this.message = message;
	}
}
