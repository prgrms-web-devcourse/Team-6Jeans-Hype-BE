package com.example.demo.model.post;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;

public class PostTest {

	private static final String musicId = "A123EB";
	private static final String albumCoverUrl = "https://localhost:8080/album/1";
	private static final String singer = "뉴진스";
	private static final String title = "Ditto";
	private static final Genre genre = Genre.ETC;
	private static final String musicUrl = "https://localhost:8080/music/1";
	private static final String content = "Hype(하입) 파이팅!!";
	private static final int likeCount = 10;
	private static final boolean isBattlePossible = true;
	private final Member member = createMember();

	@ParameterizedTest
	@NullSource
	@EmptySource
	@ValueSource(strings = {
		"             ",
		"내용이 있는 경우"
	})
	public void 성공_게시글을_생성할_수_있다(String value) {
		var post = new Post(new Music(musicId, albumCoverUrl, singer, title, genre, musicUrl),
			value, isBattlePossible, likeCount, member);
		assertThat(post).isExactlyInstanceOf(Post.class);
	}

	@Test
	public void 실패_좋아요_개수가_음수면_게시글을_생성할_수_없다() {
		assertThatThrownBy(() -> {
			new Post(new Music(musicId, albumCoverUrl, singer, title, genre, musicUrl),
				content, isBattlePossible, -1, member);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	@ValueSource(strings = {
		" ",
		"     "
	})
	public void 실패_음악_고유_번호가_존재하지_않거나_공백이면_게시글을_생성할_수_없다(String value) {
		assertThatThrownBy(() -> {
			new Post(new Music(value, albumCoverUrl, singer, title, genre, musicUrl),
				content, isBattlePossible, likeCount, member);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	@ValueSource(strings = {
		" ",
		"     "
	})
	public void 실패_앨범_커버_Url이_존재하지_않거나_공백이면_게시글을_생성할_수_없다(String value) {
		assertThatThrownBy(() -> {
			new Post(new Music(musicId, value, singer, title, genre, musicUrl),
				content, isBattlePossible, likeCount, member);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void 실패_앨범_커버_Url_길이가_2000자가_넘으면_게시글을_생성할_수_없다() {
		assertThatThrownBy(() -> {
			new Post(new Music(musicId, createOverLengthStr(), singer, title, genre, musicUrl),
				content, isBattlePossible, likeCount, member);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	@ValueSource(strings = {
		" ",
		"     "
	})
	public void 실패_가수_이름이_존재하지_않거나_공백이면_게시글을_생성할_수_없다(String value) {
		assertThatThrownBy(() -> {
			new Post(new Music(musicId, albumCoverUrl, value, title, genre, musicUrl),
				content, isBattlePossible, likeCount, member);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	@ValueSource(strings = {
		" ",
		"     "
	})
	public void 실패_음악_제목이_존재하지_않거나_공백이면_게시글을_생성할_수_없다(String value) {
		assertThatThrownBy(() -> {
			new Post(new Music(musicId, albumCoverUrl, singer, value, genre, musicUrl),
				content, isBattlePossible, likeCount, member);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void 실패_음악_장르가_존재하지_않거나_게시글을_생성할_수_없다() {
		assertThatThrownBy(() -> {
			new Post(new Music(musicId, albumCoverUrl, singer, title, null, musicUrl),
				content, isBattlePossible, likeCount, member);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	@ValueSource(strings = {
		" ",
		"     "
	})
	public void 실패_음악_Url이_존재하지_않거나_공백이면_게시글을_생성할_수_없다(String value) {
		assertThatThrownBy(() -> {
			new Post(new Music(musicId, albumCoverUrl, singer, title, genre, value),
				content, isBattlePossible, likeCount, member);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void 실패_음악_Url_길이가_2000자가_넘으면_게시글을_생성할_수_없다() {
		assertThatThrownBy(() -> {
			new Post(new Music(musicId, albumCoverUrl, singer, title, genre, createOverLengthStr()),
				content, isBattlePossible, likeCount, member);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	private String createOverLengthStr() {
		return "+".repeat(2001);
	}

	private Member createMember() {
		return Member.builder()
			.profileImageUrl("profile")
			.nickname("name")
			.refreshToken("token")
			.socialType(Social.GOOGLE)
			.socialId("socialId")
			.build();
	}
}
