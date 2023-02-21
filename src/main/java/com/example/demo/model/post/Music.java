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
	private String id;

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

	protected Music(String id, String albumCoverUrl, String singer, String title, String genreString, String musicUrl) {

		// TODO: Genre에 대한 validationd을 어떤 방식으로 해야할지 잘모르겠음.
		//  -> Genre는 value("DANCE"), name("댄스")
		//  두 가지 값이 있는데 이 두 가지 값을 넣은 이유가 받을 땐 name으로 받고 저장 시 value를 이용하겠다는 의미 라고 생각함.
		//  -> 근데 Music이 만들어질 수 있는 경우가 두 가지임 DB에서 꺼낼 때랑 게시물 등록할 떄
		//  그래서 validation check할 분기가 2번인데
		//  생성자에서 어떨 땐 name으로 찾는 메소드로 check, 어떨 땐 value로 찾는 메소드로 check 이 기준을 못나누겠음.

		checkArgument(Objects.nonNull(id), "음악 고유 번호는 Null일 수 없습니다.", id);
		checkArgument(!id.isBlank(), "음악 고유 번호는 공백일 수 없습니다.", id);

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

		this.id = id;
		this.albumCoverUrl = albumCoverUrl;
		this.singer = singer;
		this.title = title;
		this.genre = Genre.valueOf(genreString);
		this.musicUrl = musicUrl;
	}
}
