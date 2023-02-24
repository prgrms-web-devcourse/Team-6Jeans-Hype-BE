package com.example.demo.dto.genre;

import com.example.demo.model.post.Genre;

public record GenreVoResponseDto(
	String genreValue,
	String genreName
) {
	public static GenreVoResponseDto of(Genre genre) {
		return new GenreVoResponseDto(genre.name(), genre.getName());
	}
}
