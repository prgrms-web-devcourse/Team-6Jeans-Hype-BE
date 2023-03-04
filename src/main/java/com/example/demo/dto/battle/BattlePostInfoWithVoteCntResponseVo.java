package com.example.demo.dto.battle;

import com.example.demo.dto.common.MusicVoResponseDto;
import com.example.demo.model.post.Post;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BattlePostInfoWithVoteCntResponseVo(
	Long postId,
	Integer voteCnt,
	MusicVoResponseDto music
) {
	public static BattlePostInfoWithVoteCntResponseVo ofWithoutVoteCnt(Post post) {
		return BattlePostInfoWithVoteCntResponseVo.builder()
			.postId(post.getId())
			.music(MusicVoResponseDto.of(post.getMusic()))
			.build();
	}

	public static BattlePostInfoWithVoteCntResponseVo ofWithVoteCnt(Post post, int voteCnt) {
		return BattlePostInfoWithVoteCntResponseVo.builder()
			.postId(post.getId())
			.voteCnt(voteCnt)
			.music(MusicVoResponseDto.of(post.getMusic()))
			.build();
	}
}
