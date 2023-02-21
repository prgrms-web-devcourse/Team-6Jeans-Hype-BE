package com.example.demo.model.post;

import java.util.Arrays;

import com.google.common.base.Preconditions;

import lombok.Getter;

@Getter
public enum Genre {
	DANCE("댄스");

	final String name;

	Genre(String name) {
		Preconditions.checkArgument(isPresentGenre(name), "해당 노래 장르가 존재하지 않습니다.", name);
		this.name = name;
	}

	public static boolean ofValue(String genreString) {
		return Arrays.stream(Genre.values())
			.anyMatch(genre -> genre.name().equals(genreString));
	}

	private boolean isPresentGenre(String name) {
		return Arrays.stream(Genre.values())
			.anyMatch(genre -> genre.getName().equals(name));
	}
}
