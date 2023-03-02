package com.example.demo.service;

import static com.example.demo.common.ExceptionMessage.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.exception.ServerNotActiveException;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.security.oauth2.user.OAuth2UserInfo;
import com.example.demo.security.oauth2.user.OAuth2UserInfoFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final PrincipalService principalService;
	private final PostRepository postRepository;
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
			element.setRefreshToken(refreshToken);
			return element;
		}).orElseThrow(
			() -> new EntityNotFoundException(NOT_FOUND_MEMBER.getMessage())
		);
	}

	public MemberAllMyPostsResponseDto getAllPosts(
		Principal principal,
		Optional<Long> memberId,
		Optional<Genre> genre,
		Optional<Integer> limit) {

		Member member = principalService.getMemberByPrincipal(principal);

		switch (MemberFilteringCase.getCase(memberId, member.getId())) { // 마이페이지
			case MY_PAGE -> {
				return genre.map(value -> limit.map(integer -> MemberAllMyPostsResponseDto.of(
					postRepository.findAllByIdLimitAndGenreOrderByIdDesc(member.getId(), value,
						PageRequest.of(0, integer))
				)).orElseGet(() -> MemberAllMyPostsResponseDto.of(
					postRepository.findAllByMemberIdAndMusic_GenreOrderByIdDesc(member.getId(), value)
				))).orElseGet(() -> limit.map(integer -> MemberAllMyPostsResponseDto.of(
					postRepository.findAllByIdLimitOrderByIdDesc(member.getId(), PageRequest.of(0, integer))
				)).orElseGet(() -> MemberAllMyPostsResponseDto.of(
					postRepository.findAllByMemberIdOrderByIdDesc(member.getId())
				)));
			}
			case USER_PAGE -> {
				return genre.map(value -> limit.map(integer -> MemberAllMyPostsResponseDto.of(
					postRepository.findAllByIdLimitAndGenreOrderByIdDesc(memberId.get(), value,
						PageRequest.of(0, integer)))).orElseGet(() -> MemberAllMyPostsResponseDto.of(
					postRepository.findAllByMemberIdAndMusic_GenreOrderByIdDesc(memberId.get(), value)
				))).orElseGet(() -> limit.map(integer -> MemberAllMyPostsResponseDto.of(
					postRepository.findAllByIdLimitOrderByIdDesc(memberId.get(), PageRequest.of(0, integer))
				)).orElseGet(() -> MemberAllMyPostsResponseDto.of(
					postRepository.findAllByMemberIdOrderByIdDesc(memberId.get())
				)));
			}
		}

		throw new ServerNotActiveException(SERVER_ERROR.getMessage());
	}

	@Transactional
	public void updateAllMemberRanking() {
		List<Integer> points = getAllPoints();
		memberRepository.findAll()
			.forEach(member -> {
				int ranking = points.indexOf(member.getVictoryPoint()) + 1;
				member.updateRanking(ranking);
			});
	}

	@Transactional
	public void resetAllRankingAndPoint() {
		memberRepository.findAll().forEach(Member::resetRankingAndPoint);
	}

	private List<Integer> getAllPoints() {
		List<Integer> points = new ArrayList<>();
		memberRepository.findAll(Sort.by(Sort.Direction.DESC, "memberScore.victoryPoint"))
			.forEach(member -> points.add(member.getVictoryPoint()));
		return points.stream().distinct().toList();
	}
}
