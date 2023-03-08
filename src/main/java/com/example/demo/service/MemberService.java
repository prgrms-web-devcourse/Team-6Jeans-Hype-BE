package com.example.demo.service;

import static com.example.demo.common.ExceptionMessage.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.common.ResourceStorage;
import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.dto.member.MemberBattleResponseDto;
import com.example.demo.dto.member.MemberBattlesResponseDto;
import com.example.demo.dto.member.MemberUpdateResponseDto;
import com.example.demo.dto.ranking.RankersResponseDto;
import com.example.demo.exception.ServerNotActiveException;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
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

	@Value("${member.profile.image}")
	private String baseDirProfileImg;

	private final PrincipalService principalService;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final ResourceStorage resourceStorage;

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

	public MemberBattlesResponseDto getBattles(Principal principal,
		Long memberId, BattleStatus battleStatus, Genre genre, Integer limit) {

		Member member;
		if (Objects.nonNull(memberId)) {
			member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER.getMessage()));
		} else {
			member = principalService.getMemberByPrincipal(principal);
		}

		List<MemberBattleResponseDto> battles = getBattlesByMember(member);

		return MemberBattlesResponseDto.of(getFilteringBattles(battles, battleStatus, genre, limit));
	}

	private List<MemberBattleResponseDto> getBattlesByMember(Member member) {
		List<Post> posts = postRepository.findByMemberAndIsPossibleBattleIsTrue(member);

		List<Battle> battles = new ArrayList<>();
		posts.forEach(post -> {
			battles.addAll(post.getChallengedBattles());
			battles.addAll(post.getChallengingBattles());
		});

		Collections.reverse(battles);

		return battles.stream()
			.map(MemberBattleResponseDto::of)
			.toList();
	}

	private List<MemberBattleResponseDto> getFilteringBattles(List<MemberBattleResponseDto> battles,
		BattleStatus battleStatus, Genre genre, Integer limit) {

		if (Objects.nonNull(battleStatus)) {
			battles = battles.stream().filter(battle -> battle.battleStatus().equals(battleStatus)).toList();
		}

		if (Objects.nonNull(genre)) {
			battles = battles.stream().filter(battle -> battle.genre().genreValue().equals(genre)).toList();
		}

		if (Objects.nonNull(limit)) {
			if (battles.size() > limit) {
				return new ArrayList<>(battles.subList(0, limit));
			}
		}

		return battles;
	}

	public RankersResponseDto getRankerListFirstTo(int end) {
		List<Member> rankers = memberRepository.findByMemberScore_RankingBetweenOrderByMemberScore_RankingAsc(1, end);
		if (rankers.size() < end) {
			end = rankers.size();
		}
		List<Member> cuttedRankers = rankers.subList(0, end);
		return RankersResponseDto.of(cuttedRankers);
	}

	@Transactional
	public MemberUpdateResponseDto updateNickname(Principal principal, String nickname) {
		Member member = principalService.getMemberByPrincipal(principal);

		member.setNickname(nickname);
		return MemberUpdateResponseDto.of(member);
	}

	@Transactional
	public MemberUpdateResponseDto updateProfileImage(Principal principal, MultipartFile profileImage) {
		Member member = principalService.getMemberByPrincipal(principal);

		String updatedProfileImageUrl = resourceStorage.save(baseDirProfileImg, member.getId(), profileImage);
		member.setProfileImageUrl(updatedProfileImageUrl);
		return MemberUpdateResponseDto.of(member);
	}
}
