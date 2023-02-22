package com.example.demo.dto.genre;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import com.example.demo.model.post.Genre;

public class GenreAllResponseDtoTest {

	@Test
	public void 성공_모든_장르를_Dto로_바꿀수_있다() {
		// given
		var allGenres = Genre.values();
		// when
		var genreAllResponseDto = GenreAllResponseDto.toEntity(allGenres);
		// then
		assertThat(genreAllResponseDto.genres()).hasSize(allGenres.length);
	}
}
