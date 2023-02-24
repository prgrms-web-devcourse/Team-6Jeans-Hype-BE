package com.example.demo.dto.post;

import com.example.demo.model.post.Post;

import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder
public record PostFindResponseDto(
	@NonNull Long postId,
	@NonNull PostFindMusicResponseDto music,
	int likeCount,
	boolean isBattlePossible,
	String nickname
) {
	public static PostFindResponseDto of(Post post) {
		return PostFindResponseDto.builder()
			.postId(post.getId())
			.music(PostFindMusicResponseDto.of(post.getMusic()))
			.likeCount(post.getLikeCount())
			.isBattlePossible(post.isPossibleBattle())
			.nickname(post.getMember().getNickname())
			.build();
	}
}
