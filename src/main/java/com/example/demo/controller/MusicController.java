package com.example.demo.controller;

import static com.example.demo.common.ResponseMessage.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.ApiResponse;
import com.example.demo.service.MusicSearchService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/music")
public class MusicController {

	private final MusicSearchService musicSearchService;

	@GetMapping("/search")
	ResponseEntity<ApiResponse> getResultByMusicSearchApi(
		@RequestParam String term
	) {
		JsonNode search = musicSearchService.search(term);
		return ResponseEntity.ok(
			ApiResponse.success(
				SUCCESS_MUSIC_SEARCH.getMessage(),
				search
			)
		);
	}
}
