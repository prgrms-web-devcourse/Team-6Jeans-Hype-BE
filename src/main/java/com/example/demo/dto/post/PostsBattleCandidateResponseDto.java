package com.example.demo.dto.post;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder(access = AccessLevel.PRIVATE)
public record PostsBattleCandidateResponseDto(
	@NonNull List<PostBattleCandidateResponseDto> posts
) {
	public static PostsBattleCandidateResponseDto create() {
		return PostsBattleCandidateResponseDto.builder()
			.posts(new ArrayList<>())
			.build();
	}
}
