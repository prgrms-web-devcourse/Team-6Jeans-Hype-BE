package com.example.demo.model.post;

import lombok.Getter;

@Getter
public enum Genre {
	HIPHOP_RAP("힙합/랩"),
	ROCK_METAL("락/메탈"),
	INDIE_ACOUSTIC("인디/어쿠스틱"),
	BALLAD("발라드"),
	TROT("트로트"),
	K_POP("K-POP"),
	R_AND_B("R&B"),
	JAZZ("재즈"),
	J_POP("J-POP"),
	CLASSIC("클래식"),
	EDM("EDM"),
	POP("POP"),
	ETC("기타");

	final String name;

	Genre(String name) {
		this.name = name;
	}
}
