package com.example.demo.controller.genre;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.genre.GenreAllResponseDto;
import com.example.demo.model.post.Genre;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GenreAllIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void 성공_모든_장르_정보를_조회할_수_있다() {
		// given
		GenreAllResponseDto genreAllResponseDto = GenreAllResponseDto.of(Genre.values());
		// when
		ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/api/v1/genres", ApiResponse.class);
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().success()).isTrue();
		assertThat(response.getBody()).isNotNull();
	}
}
