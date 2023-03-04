package com.example.demo.dto.music;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.URL;

import com.example.demo.model.post.Music;

import lombok.Builder;

@Builder
public record SimpleMusicInfoVo(
	@NotBlank String title,
	@NotBlank String singer,
	@URL String albumUrl
) {
	public static SimpleMusicInfoVo of(Music music) {
		return SimpleMusicInfoVo.builder()
			.title(music.getTitle())
			.singer(music.getSinger())
			.albumUrl(music.getAlbumCoverUrl())
			.build();
	}
}
