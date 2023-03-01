package com.example.demo.dto.battle;

import com.example.demo.dto.common.MusicVoResponseDto;
import com.example.demo.model.post.Post;

public record BattlePostResponseDto(
	Long postId,
	MusicVoResponseDto music
) {
	public static BattlePostResponseDto of(Post post) {
		return new BattlePostResponseDto(
			post.getId(),
			MusicVoResponseDto.of(post.getMusic())
		);
	}
}
