package com.example.demo.controller;

import static com.example.demo.common.ResponseMessage.*;

import java.net.URI;
import java.security.Principal;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.dto.post.PostDetailFindResponseDto;
import com.example.demo.dto.post.PostIsLikeResponseDto;
import com.example.demo.dto.post.PostLikeResponseDto;
import com.example.demo.dto.post.PostUpdateRequestDto;
import com.example.demo.dto.post.PostsBattleCandidateResponseDto;
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
	public ResponseEntity<ApiResponse> createPost(
		Principal principal, @Valid @RequestBody PostCreateRequestDto postRequestDto) {
		Long postId = postService.createPost(principal, postRequestDto);

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

	@GetMapping("/{postId}")
	public ResponseEntity<ApiResponse> findPostById(@PathVariable("postId") Long postId) {
		PostDetailFindResponseDto post = postService.findPostById(postId);

		ApiResponse apiResponse = ApiResponse.success(SUCCESS_FIND_POST.getMessage(), post);

		return ResponseEntity.ok(apiResponse);
	}

	@GetMapping("/battle/{postId}/candidates")
	public ResponseEntity<ApiResponse> findAllBattleCandidates(
		Principal principal, @PathVariable("postId") Long postId) {
		PostsBattleCandidateResponseDto posts = postService.findAllBattleCandidates(principal, postId);

		ApiResponse apiResponse = ApiResponse.success(SUCCESS_FIND_ALL_CANDIDATE_POST.getMessage(), posts);
		return ResponseEntity.ok(apiResponse);
	}

	@PostMapping("/{postId}/like")
	public ResponseEntity<ApiResponse> likePost(Principal principal, @PathVariable("postId") Long postId) {
		PostLikeResponseDto result = postService.likePost(principal, postId);

		ApiResponse apiResponse;

		if (result.hasLike()) {
			apiResponse = ApiResponse.success(SUCCESS_LIKE_POST.getMessage(), result);
		} else {
			apiResponse = ApiResponse.success(SUCCESS_UNLIKE_POST.getMessage(), result);
		}

		return ResponseEntity.ok(apiResponse);
	}

	@GetMapping("/likes/top")
	public ResponseEntity<ApiResponse> findTenPostsByLikeCount(
		@RequestParam(name = "genre", required = false) Genre genre) {
		PostsFindResponseDto result = postService.findTenPostsByLikeCount(genre);

		ApiResponse apiResponse = ApiResponse.success(SUCCESS_FIND_ALL_POST.getMessage(), result);

		return ResponseEntity.ok(apiResponse);
	}

	@GetMapping("/{postId}/isLike")
	public ResponseEntity<ApiResponse> getPostIsLiked(
		Principal principal,
		@PathVariable("postId") Long postId) {
		PostIsLikeResponseDto result = postService.getPostIsLiked(principal, postId);

		ApiResponse apiResponse = ApiResponse.success(SUCCESS_GET_IS_LIKE.getMessage(), result);

		return ResponseEntity.ok(apiResponse);
	}

	@PatchMapping("/{postId}")
	public ResponseEntity<Void> updatePost(
		Principal principal,
		@PathVariable Long postId,
		@RequestBody PostUpdateRequestDto request
	) {

		postService.update(principal, postId, request);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
