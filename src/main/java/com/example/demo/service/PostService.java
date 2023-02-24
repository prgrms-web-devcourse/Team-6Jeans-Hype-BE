package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.dto.post.PostFindResponseDto;
import com.example.demo.dto.post.PostsFindResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

	private final PostRepository postRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public Long createPost(PostCreateRequestDto postRequestDto) {
		//TODO : 임의 유저 생성, 소셜 로그인 구현 후 삭제
		Member member = new Member("url", "name", 0, 1,
			10, 10, "token", Social.GOOGLE, "social");
		memberRepository.save(member);

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

}
