package com.example.demo.dto.member;

import com.example.demo.model.member.Member;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SimpleMemberVo(
	Long memberId,
	String nickname
) {
	public static SimpleMemberVo of(Member member) {
		return SimpleMemberVo.builder()
			.memberId(member.getId())
			.nickname(member.getNickname()).build();
	}
}
