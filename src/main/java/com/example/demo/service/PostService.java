package com.example.demo.service;

import static com.example.demo.common.ExceptionMessage.*;

import java.security.Principal;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.post.PostBattleCandidateResponseDto;
import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.dto.post.PostDetailFindResponseDto;
import com.example.demo.dto.post.PostFindResponseDto;
import com.example.demo.dto.post.PostLikeResponseDto;
import com.example.demo.dto.post.PostsBattleCandidateResponseDto;
import com.example.demo.dto.post.PostsFindResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Like;
import com.example.demo.model.post.Post;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

	private final PostRepository postRepository;
	private final LikeRepository likeRepository;
	private final PrincipalService principalService;

	@Transactional
	public Long createPost(Principal principal, PostCreateRequestDto postRequestDto) {
		Member member = principalService.getMemberByPrincipal(principal);

		boolean isExisted = postRepository.existsByMemberAndMusic_MusicId(member, postRequestDto.musicId());
		if (isExisted) {
			throw new IllegalArgumentException(DUPLICATED_USER_MUSIC_URL.getMessage());
		}

		Post post = postRequestDto.toEntity(member);
		postRepository.save(post);

		if (post.isPossibleBattle()) {
			member.addOneChallengeTicket();
		}

		return post.getId();
	}

	public PostsFindResponseDto findAllPosts(Genre genre, Boolean possible) {
		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

		switch (PostFilteringCase.getCase(genre, possible)) {
			case BOTH_NOT_NULL -> {
				return PostsFindResponseDto.of(postRepository
					.findByMusic_GenreAndIsPossibleBattle(genre, possible, sort)
					.stream().map(PostFindResponseDto::of)
					.toList());
			}
			case GENRE_ONLY_NOT_NULL -> {
				return PostsFindResponseDto.of(postRepository
					.findByMusic_Genre(genre, sort)
					.stream().map(PostFindResponseDto::of)
					.toList());
			}
			case POSSIBLE_ONLY_NOT_NULL -> {
				return PostsFindResponseDto.of(postRepository
					.findByIsPossibleBattle(possible, sort)
					.stream().map(PostFindResponseDto::of)
					.toList());
			}
			case BOTH_NULL -> {
				return PostsFindResponseDto.of(postRepository
					.findAll(sort)
					.stream().map(PostFindResponseDto::of)
					.toList());
			}
		}

		throw new IllegalArgumentException(POST_INVALID_FILTER.getMessage());
	}

	public PostDetailFindResponseDto findPostById(Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_POST.getMessage()));
		return PostDetailFindResponseDto.of(post);
	}

	public PostsBattleCandidateResponseDto findAllBattleCandidates(Principal principal, Long postId) {
		Member member = principalService.getMemberByPrincipal(principal);

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_POST.getMessage()));

		Long memberId = Long.valueOf(principal.getName());
		if (memberId.equals(post.getMember().getId())) {
			throw new IllegalArgumentException(USER_SAME_POST_WRITER.getMessage());
		}

		List<PostBattleCandidateResponseDto> posts = postRepository.findByMemberAndMusic_GenreAndIsPossibleBattleIsTrue(
				member, post.getMusic().getGenre())
			.stream().map(PostBattleCandidateResponseDto::of).toList();

		return PostsBattleCandidateResponseDto.of(posts);
	}

	@Transactional
	public PostLikeResponseDto likePost(Member member, Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_POST.getMessage()));

		boolean isExist = likeRepository.existsByMemberAndPost(member, post);

		if (isExist) {
			likeRepository.deleteByMemberAndPost(member, post);
			post.minusLike();
			return PostLikeResponseDto.of(false);
		} else {
			likeRepository.save(new Like(post, member));
			post.plusLike();
			return PostLikeResponseDto.of(true);
		}
	}
}
