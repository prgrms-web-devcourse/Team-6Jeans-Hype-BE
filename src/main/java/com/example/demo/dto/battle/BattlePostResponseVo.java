package com.example.demo.dto.battle;

import com.example.demo.dto.member.MusicVoResponseDto;
import com.example.demo.model.post.Post;

public record BattlePostResponseVo(
	Long postId,
	MusicVoResponseDto music
) {
	public static BattlePostResponseVo of(Post post) {
		return new BattlePostResponseVo(
			post.getId(),
			MusicVoResponseDto.of(post.getMusic())
		);
	}
}
