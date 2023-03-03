package com.example.demo.dto.member;

import com.example.demo.dto.common.MusicVoResponseDto;
import com.example.demo.model.post.Post;

import lombok.Builder;

@Builder
public record MemberPostVoResponseDto(
	Long postId,
	String nickname,
	boolean isPossibleBattle,
	int likeCount,
	MusicVoResponseDto music
) {
	public static MemberPostVoResponseDto of(Post post, String nickname) {
		return MemberPostVoResponseDto.builder()
			.postId(post.getId())
			.nickname(nickname)
			.isPossibleBattle(post.isPossibleBattle())
			.likeCount(post.getLikeCount())
			.music(MusicVoResponseDto.of(post.getMusic()))
			.build();
	}
}
