package com.example.demo.model.member;

import java.util.Arrays;

public enum Social {
	GOOGLE;

	public static boolean of(String socialString) {
		return Arrays.stream(Social.values())
			.anyMatch(social -> social.name().equals(socialString));
	}
}
