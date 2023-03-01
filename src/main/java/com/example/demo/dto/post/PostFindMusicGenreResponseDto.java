package com.example.demo.dto.post;

import com.example.demo.model.post.Genre;

import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder
public record PostFindMusicGenreResponseDto(
	@NonNull String genreValue,
	@NonNull String genreName
) {
	public static PostFindMusicGenreResponseDto of(Genre genre) {
		return PostFindMusicGenreResponseDto.builder()
			.genreValue(genre.toString())
			.genreName(genre.getName())
			.build();
	}
}
