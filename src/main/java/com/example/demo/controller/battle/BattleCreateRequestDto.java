package com.example.demo.controller.battle;

import javax.validation.constraints.NotNull;

import lombok.Builder;

@Builder
public record BattleCreateRequestDto(
	@NotNull long challengedPostId,
	@NotNull long challengingPostId
) {
}
