package com.example.demo.dto.member;

import java.util.List;

import com.example.demo.model.member.Member;

public record MemberAllMyPostsResponseDto(
	List<MemberPostVoResponseDto> myPosts
) {
	public static MemberAllMyPostsResponseDto of(Member member) {
		return new MemberAllMyPostsResponseDto(
			member.getPosts().stream()
				.map(post -> MemberPostVoResponseDto.of(post, member.getNickname()))
				.toList()
		);
	}
}
