package com.example.demo.service;

import java.util.Objects;

import com.example.demo.model.post.Genre;

public enum PostFilteringCase {
	BOTH_NOT_NULL,
	GENRE_ONLY_NOT_NULL,
	POSSIBLE_ONLY_NOT_NULL,
	BOTH_NULL;

	public static PostFilteringCase getCase(Genre genre, boolean isBattlePossible) {
		if (Objects.nonNull(genre) && Objects.nonNull(isBattlePossible)) {
			return BOTH_NOT_NULL;
		} else if (Objects.nonNull(genre)) {
			return GENRE_ONLY_NOT_NULL;
		} else if (Objects.nonNull(isBattlePossible)) {
			return POSSIBLE_ONLY_NOT_NULL;
		} else {
			return BOTH_NULL;
		}
	}
}
