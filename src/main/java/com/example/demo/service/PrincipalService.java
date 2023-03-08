package com.example.demo.service;

import java.security.Principal;

import javax.persistence.EntityNotFoundException;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
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
		try {
			Long id = Long.valueOf(principal.getName());
			return memberRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_FOUND_MEMBER.getMessage()));
		} catch (NumberFormatException | NullPointerException exception) {
			throw new AuthenticationCredentialsNotFoundException(ExceptionMessage.CANNOT_ACCESS_ANONYMOUS.getMessage());
		}

	}
}
