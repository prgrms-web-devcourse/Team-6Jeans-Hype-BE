package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.genre.GenreAllResponseDto;
import com.example.demo.model.post.Genre;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/genres")
public class GenreController {

	@GetMapping
	public ResponseEntity<ApiResponse> getAllGenres() {
		GenreAllResponseDto genreAllResponseDto = GenreAllResponseDto.of(Genre.values());
		return ResponseEntity.ok(
			ApiResponse.success(
				"장르 전체 조회 성공",
				genreAllResponseDto)
		);
	}
}
