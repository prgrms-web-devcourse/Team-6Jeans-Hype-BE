package com.example.demo.dto.post;

import com.example.demo.model.post.Post;

import lombok.AccessLevel;
import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder(access = AccessLevel.PRIVATE)
public record PostDetailFindResponseDto(
	@NonNull Long memberId,
	@NonNull PostDetailFindMusicResponseDto music,
	@NonNull String content,
	boolean isBattlePossible,
	@NonNull String nickname,
	int likeCount
) {
	public static PostDetailFindResponseDto of(Post post) {
		return PostDetailFindResponseDto.builder()
			.memberId(post.getMember().getId())
			.music(PostDetailFindMusicResponseDto.of(post.getMusic()))
			.content(post.getContent())
			.isBattlePossible(post.isPossibleBattle())
			.nickname(post.getMember().getNickname())
			.likeCount(post.getLikeCount())
			.build();
	}
}
