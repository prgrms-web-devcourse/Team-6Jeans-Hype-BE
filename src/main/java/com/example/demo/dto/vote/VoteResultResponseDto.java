package com.example.demo.dto.vote;

public record VoteResultResponseDto(
	String title,
	String albumCoverUrl,
	int selectedPostVoteCnt,
	int oppositePostVoteCnt
) {
}
