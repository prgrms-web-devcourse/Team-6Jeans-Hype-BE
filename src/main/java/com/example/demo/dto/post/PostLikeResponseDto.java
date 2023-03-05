package com.example.demo.dto.post;

import lombok.AccessLevel;
import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder(access = AccessLevel.PRIVATE)
public record PostLikeResponseDto(
	@NonNull Boolean hasLike
) {
	public static PostLikeResponseDto of(Boolean hasLike) {
		return PostLikeResponseDto.builder()
			.hasLike(hasLike)
			.build();
	}
}
