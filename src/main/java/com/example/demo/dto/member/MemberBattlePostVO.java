package com.example.demo.dto.member;

import com.example.demo.model.post.Post;

import lombok.Builder;
import reactor.util.annotation.NonNull;

@Builder
public record MemberBattlePostVO(
	@NonNull String title,
	@NonNull String singer,
	@NonNull String albumUrl,
	@NonNull String nickname) {

	public static MemberBattlePostVO of(Post post) {
		return MemberBattlePostVO.builder()
			.title(post.getMusic().getTitle())
			.singer(post.getMusic().getSinger())
			.albumUrl(post.getMusic().getAlbumCoverUrl())
			.nickname(post.getMember().getNickname())
			.build();
	}
}
