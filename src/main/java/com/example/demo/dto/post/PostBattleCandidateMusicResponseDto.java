package com.example.demo.dto.post;

import com.example.demo.model.post.Music;

import lombok.AccessLevel;
import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder(access = AccessLevel.PRIVATE)
public record PostBattleCandidateMusicResponseDto(
	@NonNull String musicName,
	@NonNull String albumCoverUrl,
	@NonNull String singer
) {
	public static PostBattleCandidateMusicResponseDto of(Music music) {
		return PostBattleCandidateMusicResponseDto.builder()
			.musicName(music.getTitle())
			.albumCoverUrl(music.getAlbumCoverUrl())
			.singer(music.getSinger())
			.build();
	}
}
