package com.example.demo.model.post;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class PostTest {

	private static final String musicId = "A123EB";
	private static final String albumCoverUrl = "https://localhost:8080/album/1";
	private static final String singer = "뉴진스";
	private static final String title = "Ditto";
	private static final Genre genre = Genre.DANCE;
	private static final String musicUrl = "https://localhost:8080/music/1";
	private static final String content = "Hype(하입) 파이팅!!";
	private static final int likeCount = 10;

	@ParameterizedTest
	@NullSource
	@EmptySource
	@ValueSource(strings = {
		"             ",
		"내용이 있는 경우"
	})
	public void 성공_Post를_생성할_수_있다(String value) {
		var post = new Post(musicId, albumCoverUrl, singer, title, genre, musicUrl, value, likeCount);
		assertThat(post).isExactlyInstanceOf(Post.class);
	}

	@Test
	public void 실패_좋아요_개수가_음수면_Post를_생성할_수_없다() {
		assertThatThrownBy(() -> {
			new Post(musicId, albumCoverUrl, singer, title, genre, musicUrl, content, -1);
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
	public void 실패_음악_고유_번호가_null이거나_공백이면_Post를_생성할_수_없다(String value) {
		assertThatThrownBy(() -> {
			new Post(value, albumCoverUrl, singer, title, genre, musicUrl, content, likeCount);
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
	public void 실패_앨범_커버_Url이_null이거나_공백이면_Post를_생성할_수_없다(String value) {
		assertThatThrownBy(() -> {
			new Post(musicId, value, singer, title, genre, musicUrl, content, likeCount);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void 실패_앨범_커버_Url_길이가_2000자가_넘으면_Post를_생성할_수_없다() {
		assertThatThrownBy(() -> {
			new Post(musicId, createOverLengthStr(), singer, title, genre, musicUrl, content, likeCount);
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
	public void 실패_가수_이름이_null이거나_공백이면_Post를_생성할_수_없다(String value) {
		assertThatThrownBy(() -> {
			new Post(musicId, albumCoverUrl, value, title, genre, musicUrl, content, likeCount);
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
	public void 실패_음악_제목이_null이거나_공백이면_Post를_생성할_수_없다(String value) {
		assertThatThrownBy(() -> {
			new Post(musicId, albumCoverUrl, singer, value, genre, musicUrl, content, likeCount);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void 실패_음악_장르가_null이면_Post를_생성할_수_없다() {
		assertThatThrownBy(() -> {
			new Post(musicId, albumCoverUrl, singer, title, null, musicUrl, content, likeCount);
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
	public void 실패_음악_Url이_null이거나_공백이면_Post를_생성할_수_없다(String value) {
		assertThatThrownBy(() -> {
			new Post(musicId, albumCoverUrl, singer, title, genre, value, content, likeCount);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void 실패_음악_Url_길이가_2000자가_넘으면_Post를_생성할_수_없다() {
		assertThatThrownBy(() -> {
			new Post(musicId, albumCoverUrl, singer, title, genre, createOverLengthStr(), content, likeCount);
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	private String createOverLengthStr() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= 2000; i++) {
			sb.append("+");
		}
		return sb.toString();
	}
}
