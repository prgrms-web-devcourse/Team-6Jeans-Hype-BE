package com.example.demo.dto.common;

import com.example.demo.dto.genre.GenreVoResponseDto;
import com.example.demo.model.post.Music;

import lombok.Builder;

@Builder
public record MusicVoResponseDto(
	String musicId,
	String singer,
	String title,
	String musicUrl,
	String albumCoverUrl,
	GenreVoResponseDto genre
) {
	public static MusicVoResponseDto of(Music music) {
		return MusicVoResponseDto.builder()
			.musicId(music.getMusicId())
			.singer(music.getSinger())
			.title(music.getTitle())
			.musicUrl(music.getMusicUrl())
			.albumCoverUrl(music.getAlbumCoverUrl())
			.genre(GenreVoResponseDto.of(music.getGenre()))
			.build();
	}
}
