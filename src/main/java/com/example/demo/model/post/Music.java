package com.example.demo.model.post;

import static com.google.common.base.Preconditions.*;

import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Music {

	@NotBlank
	private String musicId;

	@NotBlank
	@Length(max = 2000)
	String albumCoverUrl;

	@NotBlank
	private String singer;

	@NotBlank
	private String title;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	private Genre genre;

	@NotBlank
	@Length(max = 2000)
	private String musicUrl;

	protected Music(String musicId, String albumCoverUrl, String singer, String title, Genre genre, String musicUrl) {
		checkArgument(Objects.nonNull(musicId), "음악 고유 번호는 Null일 수 없습니다.", musicId);
		checkArgument(!musicId.isBlank(), "음악 고유 번호는 공백일 수 없습니다.", musicId);

		checkArgument(Objects.nonNull(albumCoverUrl), "앨범 커버 이미지 URL은 Null일 수 없습니다.", albumCoverUrl);
		checkArgument(!albumCoverUrl.isBlank(), "앨범 커버 이미지 URL은 공백일 수 없습니다.", albumCoverUrl);
		checkArgument(albumCoverUrl.length() <= 2000, "앨범 커버 이미지 URL은 2000자보다 더 길 수 없습니다.", albumCoverUrl);

		checkArgument(Objects.nonNull(singer), "가수 이름은 Null일 수 없습니다.", singer);
		checkArgument(!singer.isBlank(), "가수 이름은 공백일 수 없습니다.", singer);

		checkArgument(Objects.nonNull(title), "음악 제목은 Null일 수 없습니다.", title);
		checkArgument(!title.isBlank(), "음악 제목은 공백일 수 없습니다.", title);

		checkArgument(Objects.nonNull(musicUrl), "음악 URL은 Null일 수 없습니다.", musicUrl);
		checkArgument(!musicUrl.isBlank(), "음악 URL은 공백일 수 없습니다.", musicUrl);
		checkArgument(musicUrl.length() <= 2000, "음악 URL은 2000자보다 더 길 수 없습니다.", musicUrl);

		this.musicId = musicId;
		this.albumCoverUrl = albumCoverUrl;
		this.singer = singer;
		this.title = title;
		this.genre = genre;
		this.musicUrl = musicUrl;
	}
}
