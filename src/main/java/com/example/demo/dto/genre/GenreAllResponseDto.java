package com.example.demo.dto.genre;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.post.Genre;

public record GenreAllResponseDto(
	List<GenreVoResponseDto> genres
) {
	public static GenreAllResponseDto of(Genre... genres) {
		return new GenreAllResponseDto(
			Arrays.stream(genres)
				.map(GenreVoResponseDto::of)
				.collect(Collectors.toList())
		);
	}
}
