package com.example.demo.dto.genre;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.post.Genre;

public record GenreAllResponseDto(
	List<GenreObjectResponseDto> genres
) {
	public static GenreAllResponseDto toEntity(Genre... genres) {
		return new GenreAllResponseDto(
			Arrays.stream(genres)
				.map(GenreObjectResponseDto::toObject)
				.collect(Collectors.toList())
		);
	}
}
