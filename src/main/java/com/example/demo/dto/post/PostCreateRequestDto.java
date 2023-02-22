package com.example.demo.dto.post;

import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;

import reactor.util.annotation.NonNull;

public record PostCreateRequestDto(
	@NonNull String musicId,
	@NonNull String musicName,
	@NonNull String musicUrl,
	@NonNull String albumCoverUrl,
	@NonNull Genre genre,
	@NonNull String singer,
	boolean isBattlePossible,
	String content
) {

	public Post toEntity(Member member) {
		return Post
			.create(musicId, albumCoverUrl, singer, musicName, genre, musicUrl, content, isBattlePossible, member);
	}
}
