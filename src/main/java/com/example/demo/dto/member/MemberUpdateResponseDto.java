package com.example.demo.dto.member;

import com.example.demo.model.member.Member;

public record MemberUpdateResponseDto(
	String nickname,
	String profileImageUrl
) {

	public static MemberUpdateResponseDto of(Member member) {
		return new MemberUpdateResponseDto(
			member.getNickname(),
			member.getProfileImageUrl()
		);
	}
}
