package com.example.demo.dto.post;

import lombok.Builder;

@Builder
public record PostIsLikeResponseDto(
	boolean isLiked
) {
}
