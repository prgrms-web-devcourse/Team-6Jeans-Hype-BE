package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
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
}
