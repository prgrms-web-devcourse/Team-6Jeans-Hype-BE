package com.example.demo.dto.member;

import com.example.demo.model.post.Genre;

import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder
public record MemberBattleGenre(
	@NonNull Genre genreValue,
	@NonNull String genreName
) {

	public static MemberBattleGenre of(Genre genre) {
		return MemberBattleGenre.builder()
			.genreValue(genre)
			.genreName(genre.getName())
			.build();
	}
}
