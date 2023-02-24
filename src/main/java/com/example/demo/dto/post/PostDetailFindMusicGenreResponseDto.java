package com.example.demo.dto.post;

import com.example.demo.model.post.Genre;

import lombok.AccessLevel;
import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder(access = AccessLevel.PRIVATE)
public record PostDetailFindMusicGenreResponseDto(
	@NonNull String genreValue,
	@NonNull String genreName
) {
	public static PostDetailFindMusicGenreResponseDto of(Genre genre) {
		return PostDetailFindMusicGenreResponseDto.builder()
			.genreValue(genre.toString())
			.genreName(genre.getName())
			.build();
	}
}
