package com.example.demo.common;

import java.security.Principal;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Component;

import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PrincipalHandler {

	private final MemberRepository memberRepository;

	public Member getMemberByPrincipal(Principal principal) {
		return memberRepository.findById(Long.valueOf(principal.getName()))
			.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));
	}
}
