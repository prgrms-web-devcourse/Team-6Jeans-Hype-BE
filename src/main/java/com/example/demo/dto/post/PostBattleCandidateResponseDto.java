package com.example.demo.dto.post;

import com.example.demo.model.post.Post;

import lombok.AccessLevel;
import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder(access = AccessLevel.PRIVATE)
public record PostBattleCandidateResponseDto(
	@NonNull Long postId,
	@NonNull PostBattleCandidateMusicResponseDto music
) {
	public static PostBattleCandidateResponseDto of(Post post) {
		return PostBattleCandidateResponseDto.builder()
			.postId(post.getId())
			.music(PostBattleCandidateMusicResponseDto.of(post.getMusic()))
			.build();
	}

	public static PostBattleCandidateResponseDto testFrom(Post post) {
		return PostBattleCandidateResponseDto.builder()
			.postId(0L)
			.music(PostBattleCandidateMusicResponseDto.of(post.getMusic()))
			.build();
	}
}
