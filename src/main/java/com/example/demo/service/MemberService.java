package com.example.demo.service;

import static com.example.demo.common.ExceptionMessage.*;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.repository.MemberRepository;
import com.example.demo.security.oauth2.user.OAuth2UserInfo;
import com.example.demo.security.oauth2.user.OAuth2UserInfoFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	@Transactional
	public long join(OAuth2User oauth2User, String socialName) {
		Social socialType = Social.valueOf(socialName.toUpperCase());
		String socialId = oauth2User.getName();
		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(socialName, oauth2User.getAttributes());
		Member foundedMember = memberRepository.findBySocialInfo_SocialTypeAndSocialInfo_SocialId(socialType, socialId)
			.map(member -> {
				//user가 전에 로그인 한 적 있음
				log.info("Already exists: {} for (social: {}, socialId: {})", member, socialType, socialId);
				return member;
			})
			.orElseGet(() -> {
				//처음 로그인 하면
				log.info("첫 로그인 감지. 자동 회원가입을 진행합니다.");
				return memberRepository.save(userInfo.toEntity());
			});
		return foundedMember.getId();
	}

	@Transactional
	public void assignRefreshToken(long memberId, String refreshToken) {
		Optional<Member> member = memberRepository.findById(memberId);
		member.map(element -> {
			element.setRefreshTken(refreshToken);
			return element;
		}).orElseThrow(
			() -> new EntityNotFoundException(NOT_FOUND_MEMBER.getMessage())
		);
	}

	public MemberAllMyPostsResponseDto getAllPosts(Member member) {
		return MemberAllMyPostsResponseDto.of(member);
	}
}
