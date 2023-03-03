package com.example.demo.dto.member;

import java.util.List;

import com.example.demo.model.member.Member;
import com.example.demo.model.post.Post;

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

	public static MemberAllMyPostsResponseDto of(List<Post> posts) {
		return new MemberAllMyPostsResponseDto(
			posts.stream()
				.map(post -> MemberPostVoResponseDto.of(post, post.getMember().getNickname()))
				.toList()
		);
	}
}
