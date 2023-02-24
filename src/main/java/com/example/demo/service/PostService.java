package com.example.demo.service;

import static com.example.demo.common.ExceptionMessage.*;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.post.PostBattleCandidateResponseDto;
import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.dto.post.PostDetailFindResponseDto;
import com.example.demo.dto.post.PostFindResponseDto;
import com.example.demo.dto.post.PostsBattleCandidateResponseDto;
import com.example.demo.dto.post.PostsFindResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

	private final PostRepository postRepository;

	@Transactional
	public Long createPost(Member member, PostCreateRequestDto postRequestDto) {
		Post post = postRequestDto.toEntity(member);
		postRepository.save(post);
		return post.getId();
	}

	public PostsFindResponseDto findAllPosts(Genre genre, Boolean possible) {
		PostsFindResponseDto postsDto = PostsFindResponseDto.create();

		switch (PostFilteringCase.getCase(genre, possible)) {
			case BOTH_NOT_NULL -> postRepository
				.findByMusic_GenreAndIsPossibleBattle(genre, possible)
				.forEach(post -> postsDto.posts().add(PostFindResponseDto.of(post)));
			case GENRE_ONLY_NOT_NULL -> postRepository
				.findByMusic_Genre(genre)
				.forEach(post -> postsDto.posts().add(PostFindResponseDto.of(post)));
			case POSSIBLE_ONLY_NOT_NULL -> postRepository
				.findByIsPossibleBattle(possible)
				.forEach(post -> postsDto.posts().add(PostFindResponseDto.of(post)));
			case BOTH_NULL -> postRepository
				.findAll()
				.forEach(post -> postsDto.posts().add(PostFindResponseDto.of(post)));
		}

		return postsDto;
	}

	public PostDetailFindResponseDto findPostById(Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_POST.getMessage()));
		return PostDetailFindResponseDto.of(post);
	}

	public PostsBattleCandidateResponseDto findAllBattleCandidates(Member member, Genre genre) {
		PostsBattleCandidateResponseDto posts = PostsBattleCandidateResponseDto.create();
		postRepository.findByMemberAndMusic_GenreAndIsPossibleBattleIsTrue(member, genre)
			.forEach(post -> posts.posts().add(PostBattleCandidateResponseDto.of(post)));
		return posts;
	}

}
