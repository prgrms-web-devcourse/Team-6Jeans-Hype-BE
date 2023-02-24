package com.example.demo.service;

import java.security.Principal;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.example.demo.common.ExceptionMessage;
import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalService {

	private final MemberRepository memberRepository;

	public Member getMemberByPrincipal(Principal principal) {
		return memberRepository.findById(Long.valueOf(principal.getName()))
			.orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_FOUND_MEMBER.getMessage()));
	}
}
