package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberAllMyPostsResponseDto getAllPosts(Member member) {
		return MemberAllMyPostsResponseDto.of(member);
	}
}
