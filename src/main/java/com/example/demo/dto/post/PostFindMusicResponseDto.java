package com.example.demo.dto.post;

import com.example.demo.model.post.Music;

import lombok.AccessLevel;
import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder(access = AccessLevel.PRIVATE)
public record PostFindMusicResponseDto(
	@NonNull String title,
	@NonNull String albumCoverUrl,
	@NonNull String singer,
	@NonNull PostFindMusicGenreResponseDto genre
) {
	public static PostFindMusicResponseDto of(Music music) {
		return PostFindMusicResponseDto.builder()
			.title(music.getTitle())
			.albumCoverUrl(music.getAlbumCoverUrl())
			.singer(music.getSinger())
			.genre(PostFindMusicGenreResponseDto.of(music.getGenre()))
			.build();
	}
}
