package com.example.demo.dto.music;

import java.util.List;

public record MusicSearchResponseDto(
	int resultCount,
	List<MusicSearchResponseVo> results
) {
	public static MusicSearchResponseDto of(List<MusicSearchResponseVo> musicList) {
		return new MusicSearchResponseDto(
			musicList.size(),
			musicList
		);
	}
}
