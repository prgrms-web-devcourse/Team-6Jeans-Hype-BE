package com.example.demo.dto.genre;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import com.example.demo.model.post.Genre;

public class GenreObjectResponseDtoTest {

	@Test
	public void 성공_장르를_Dto로_변환할_수_있다() {
		// given
		Genre genre = Genre.ETC;
		// when
		var dto = GenreObjectResponseDto.toObject(genre);
		// then
		assertThat(dto).isNotNull();
		assertThat(dto.genreValue()).isEqualTo(genre.name());
		assertThat(dto.genreName()).isEqualTo(genre.getName());
	}
}
