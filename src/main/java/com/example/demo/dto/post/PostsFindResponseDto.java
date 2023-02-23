package com.example.demo.dto.post;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PostsFindResponseDto(
	List<PostFindResponseDto> posts
) {
	public static PostsFindResponseDto create() {
		return PostsFindResponseDto.builder()
			.posts(new ArrayList<>())
			.build();
	}
}
