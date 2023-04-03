package com.example.demo.dto.post;

import com.example.demo.model.post.Genre;

import lombok.Builder;
import reactor.util.annotation.Nullable;

@Builder
public record PostUpdateRequestDto(
	@Nullable String musicId,
	@Nullable String title,
	@Nullable String musicUrl,
	@Nullable String albumCoverUrl,
	@Nullable Genre genre,
	@Nullable String singer,
	@Nullable Boolean battlePossible,
	@Nullable String content
) {

	public boolean isAllDataNull() {
		return musicId == null && title == null && musicUrl == null && albumCoverUrl == null && genre == null
			&& singer == null && battlePossible == null && content == null;
	}

	public boolean isMusicDataNull() {
		return musicId == null && title == null && musicUrl == null && albumCoverUrl == null && genre == null
			&& singer == null;
	}
}
