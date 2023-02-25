package com.example.demo.model.post;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;

public class LikeTest {

	@Test
	public void 성공_Like를_생성할_수_있다() {
		// given
		var post = createPost();
		var member = createMember();

		// when
		var like = new Like(post, member);

		// then
		assertThat(like).isExactlyInstanceOf(Like.class);
	}

	@ParameterizedTest
	@NullSource
	public void 실패_게시글이_존재하지_않으면_Like를_생성할_수_없다(Post notPresentPost) {
		var member = createMember();
		assertThatThrownBy(() -> new Like(notPresentPost, member))
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullSource
	public void 실패_멤버가_존재하지_않으면_Like를_생성할_수_없다(Member notPresentMember) {
		var post = createPost();
		assertThatThrownBy(() -> new Like(post, notPresentMember))
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	private Post createPost() {
		String musicId = "A123EB";
		String albumCoverUrl = "https://localhost:8080/album/1";
		String singer = "뉴진스";
		String title = "Ditto";
		Genre genre = Genre.K_POP;
		String musicUrl = "https://localhost:8080/music/1";
		boolean isBattlePossible = true;
		String content = "Hype(하입) 파이팅!!";
		int likeCount = 10;
		Music music = new Music(musicId, albumCoverUrl, singer, title, genre, musicUrl);
		return new Post(music, content, isBattlePossible, likeCount, createMember());
	}

	private Member createMember() {
		return Member.builder()
			.profileImageUrl("https://localhost:8080/profile/1")
			.nickname("A씨")
			.refreshToken("refreshToken")
			.socialType(Social.GOOGLE)
			.socialId("socialId")
			.build();
	}
}
