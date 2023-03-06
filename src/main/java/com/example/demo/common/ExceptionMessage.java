package com.example.demo.common;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
	// EntityNotFoundException
	NOT_FOUND_POST("존재하지 않는 게시글입니다."),
	NOT_FOUND_MEMBER("존재하지 않는 유저입니다."),
	OBJECT_NOT_NULL("은(는) Null 일 수 없습니다."),
	CANNOT_MAKE_BATTLE_WRONG_POST_ID("대결을 생성할 수 있는 알맞는 PostId를 찾을 수 없습니다."),
	// IllegalArgumentException
	DUPLICATED_USER_MUSIC_URL("이미 공유한 곡입니다."),
	DUPLICATED_USER_VOTE("이미 투표한 대결입니다."),
	SERVER_ERROR("서버 내부에 오류가 발생했습니다."),
	NOT_FOUND_BATTLE("존재하지 않는 대결입니다."),
	POST_NOT_CONTAIN_BATTLE("투표한 게시글이 대결에 존재하지 않습니다."),
	POST_INVALID_FILTER("잘못된 필터링 요청 값입니다."),
	CANNOT_MAKE_BATTLE_NOT_MEMBERS_POST("선택한 post가 사용자가 소유하고 있는 Post가 아닙니다."),
	CANNOT_MAKE_BATTLE_OWN_CHALLENED_POST("자신의 post에 배틀을 신청할 수 없습니다."),
	CANNOT_MAKE_BATTLE_DIFFERENT_GENRE("두 post의 음악 장르가 다릅니다."),
	CANNOT_MAKE_BATTLE_SAME_MUSIC("두 Post가 같은 음악에 대한 Post입니다."),
	//IllegalStateException
	CANNOT_MAKE_BATTLE_NOT_ENOUGH_CHALLENGE_TICKET("사용자의 대결권이 부족합니다."),
	USER_SAME_POST_WRITER("직접 작성한 추천글은 대결신청할 수 없습니다.");

	final String message;

	ExceptionMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
