package com.example.demo.dto.member;

import com.example.demo.model.post.Genre;

import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder
public record MemberBattleGenreVO(
	@NonNull Genre genreValue,
	@NonNull String genreName
) {

	public static MemberBattleGenreVO of(Genre genre) {
		return MemberBattleGenreVO.builder()
			.genreValue(genre)
			.genreName(genre.getName())
			.build();
	}
}
