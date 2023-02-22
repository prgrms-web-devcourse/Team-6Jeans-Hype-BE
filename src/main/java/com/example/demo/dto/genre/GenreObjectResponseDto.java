package com.example.demo.dto.genre;

import com.example.demo.model.post.Genre;

public record GenreObjectResponseDto(
	String genreValue,
	String genreName
) {
	public static GenreObjectResponseDto toObject(Genre genre) {
		return new GenreObjectResponseDto(genre.name(), genre.getName());
	}
}
