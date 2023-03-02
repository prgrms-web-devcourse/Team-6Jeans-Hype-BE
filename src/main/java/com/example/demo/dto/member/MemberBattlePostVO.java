package com.example.demo.dto.member;

import com.example.demo.model.post.Post;

import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder
public record MemberBattlePostVO(
	@NonNull String title,
	@NonNull String albumUrl,
	@NonNull String nickname) {

	public static MemberBattlePostVO of(Post post) {
		return MemberBattlePostVO.builder()
			.title(post.getMusic().getTitle())
			.albumUrl(post.getMusic().getAlbumCoverUrl())
			.nickname(post.getMember().getNickname())
			.build();
	}
}
