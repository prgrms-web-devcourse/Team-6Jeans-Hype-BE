package com.example.demo.dto.post;

import com.example.demo.model.post.Music;

import lombok.AccessLevel;
import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder(access = AccessLevel.PRIVATE)
public record PostDetailFindMusicResponseDto(
	@NonNull String musicName,
	@NonNull String musicUrl,
	@NonNull String albumCoverUrl,
	@NonNull String singer,
	@NonNull PostDetailFindMusicGenreResponseDto genre
) {
	public static PostDetailFindMusicResponseDto from(Music music) {
		return PostDetailFindMusicResponseDto.builder()
			.musicName(music.getTitle())
			.musicUrl(music.getMusicUrl())
			.albumCoverUrl(music.getAlbumCoverUrl())
			.singer(music.getSinger())
			.genre(PostDetailFindMusicGenreResponseDto.from(music.getGenre()))
			.build();
	}
}
