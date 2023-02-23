package com.example.demo.controller;

import static com.example.demo.common.ResponseMessage.*;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.dto.post.PostsFindResponseDto;
import com.example.demo.model.post.Genre;
import com.example.demo.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

	private final PostService postService;

	@PostMapping
	public ResponseEntity<ApiResponse> createPost(@RequestBody PostCreateRequestDto postRequestDto) {
		Long postId = postService.createPost(postRequestDto);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{postId}")
			.buildAndExpand(postId)
			.toUri();

		ApiResponse apiResponse = ApiResponse.success(SUCCESS_CREATE_POST.getMessage());

		return ResponseEntity.created(location).body(apiResponse);
	}

	@GetMapping
	public ResponseEntity<ApiResponse> findAllPosts(@RequestParam(name = "genre", required = false) Genre genre,
		@RequestParam(name = "possible", required = false) Boolean possible) {
		PostsFindResponseDto posts = postService.findAllPosts(genre, possible);

		ApiResponse apiResponse = ApiResponse.success(SUCCESS_FIND_ALL_POST.getMessage(), posts);
		return ResponseEntity.ok(apiResponse);
	}

}
