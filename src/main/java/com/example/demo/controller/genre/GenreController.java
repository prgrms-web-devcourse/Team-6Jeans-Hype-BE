package com.example.demo.controller.genre;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.genre.GenreAllResponseDto;
import com.example.demo.model.post.Genre;

@RestController(value = "/api/v1")
public class GenreController {

	@GetMapping("/genres")
	public ResponseEntity<ApiResponse> getAllGenres() {
		GenreAllResponseDto genreAllResponseDto = GenreAllResponseDto.toEntity(Genre.values());
		return ResponseEntity.ok(
			ApiResponse.success(
				"장르 전체 조회 성공",
				genreAllResponseDto)
		);
	}
}
