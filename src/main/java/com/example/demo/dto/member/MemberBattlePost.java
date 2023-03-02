package com.example.demo.dto.member;

import com.example.demo.model.post.Post;

import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder
public record MemberBattlePost(
	@NonNull String title,
	@NonNull String albumUrl,
	@NonNull String nickname) {

	public static MemberBattlePost of(Post post) {
		return MemberBattlePost.builder()
			.title(post.getMusic().getTitle())
			.albumUrl(post.getMusic().getAlbumCoverUrl())
			.nickname(post.getMember().getNickname())
			.build();
	}
}
