package com.example.demo.dto.post;

import com.example.demo.model.post.Music;

import lombok.AccessLevel;
import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder(access = AccessLevel.PRIVATE)
public record PostBattleCandidateMusicResponseDto(
	@NonNull String title,
	@NonNull String albumCoverUrl,
	@NonNull String singer,
	@NonNull String musicUrl
) {
	public static PostBattleCandidateMusicResponseDto of(Music music) {
		return PostBattleCandidateMusicResponseDto.builder()
			.title(music.getTitle())
			.albumCoverUrl(music.getAlbumCoverUrl())
			.singer(music.getSinger())
			.musicUrl(music.getMusicUrl())
			.build();
	}
}
