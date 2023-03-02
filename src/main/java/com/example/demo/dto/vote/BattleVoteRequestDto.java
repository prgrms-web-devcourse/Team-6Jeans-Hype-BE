package com.example.demo.dto.vote;

import javax.validation.constraints.NotNull;

public record BattleVoteRequestDto(
	@NotNull Long battleId,
	@NotNull Long votedPostId
) {
}
