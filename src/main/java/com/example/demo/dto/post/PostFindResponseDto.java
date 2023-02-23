package com.example.demo.dto.post;

import com.example.demo.model.post.Post;

import lombok.AccessLevel;
import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder(access = AccessLevel.PRIVATE)
public record PostFindResponseDto(
	@NonNull Long postId,
	@NonNull PostFindMusicResponseDto music,
	int likeCount,
	boolean isBattlePossible,
	String nickname
) {
	public static PostFindResponseDto from(Post post) {
		return PostFindResponseDto.builder()
			.postId(post.getId())
			.music(PostFindMusicResponseDto.from(post.getMusic()))
			.likeCount(post.getLikeCount())
			.isBattlePossible(post.isPossibleBattle())
			.nickname(post.getMember().getNickname())
			.build();
	}

	public static PostFindResponseDto testFrom(Post post) {
		return PostFindResponseDto.builder()
			.postId(0L)
			.music(PostFindMusicResponseDto.from(post.getMusic()))
			.likeCount(post.getLikeCount())
			.isBattlePossible(post.isPossibleBattle())
			.nickname(post.getMember().getNickname())
			.build();
	}
}
