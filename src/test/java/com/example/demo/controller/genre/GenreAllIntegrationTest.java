package com.example.demo.controller.genre;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.genre.GenreAllResponseDto;
import com.example.demo.model.post.Genre;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GenreAllIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private final GenreController genreController = new GenreController();

	@LocalServerPort
	private int port;

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
