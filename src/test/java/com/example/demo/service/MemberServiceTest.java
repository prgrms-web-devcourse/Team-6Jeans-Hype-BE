package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.example.demo.dto.member.MemberBattleGenreVO;
import com.example.demo.dto.member.MemberBattlePostVO;
import com.example.demo.dto.member.MemberBattleResponseDto;
import com.example.demo.dto.member.MemberBattlesResponseDto;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;
	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PrincipalService principalService;

	@Mock
	private PostRepository postRepository;

	@Mock
	private Principal principal;

	private final Member member = createMember();
	private final BattleStatus status = BattleStatus.PROGRESS;
	private final Genre genre = Genre.K_POP;

	@Test
	void 승리_포인트_기준으로_유저의_랭킹을_업데이트할_수_있다() {
		// given
		List<Member> members = getMembers();
		List<Integer> points = new ArrayList<>();
		for (int i = 0; i < members.size(); i++) {
			points.add((i + 1) * 10);
		}
		for (int i = 0; i < members.size(); i++) {
			members.get(i).plusPoint(points.get(i));
		}

		// when
		when(memberRepository.findAll()).thenReturn(members);
		when(memberRepository.findAll(Sort.by(Sort.Direction.DESC, "memberScore.victoryPoint")))
			.thenReturn(members);

		memberService.updateAllMemberRanking();

		// then
		for (Member member : members) {
			assertThat(member.getRanking())
				.isEqualTo(points.indexOf(member.getMemberScore().getVictoryPoint()) + 1);
		}

		verify(memberRepository).findAll();
		verify(memberRepository).findAll(Sort.by(Sort.Direction.DESC, "memberScore.victoryPoint"));
	}

	@Test
	void 모든_유저의_랭킹과_승리_포인트를_0으로_리셋할_수_있다() {
		// given
		List<Member> members = getMembers();
		for (int i = 0; i < members.size(); i++) {
			members.get(i).plusPoint((i + 1) * 10);
			members.get(i).updateRanking(i + 1);
		}

		// when
		when(memberRepository.findAll()).thenReturn(members);

		memberService.resetAllRankingAndPoint();

		// then
		for (Member member : members) {
			assertThat(member.getRanking()).isEqualTo(0);
			assertThat(member.getVictoryPoint()).isEqualTo(0);
		}

		verify(memberRepository).findAll();
	}

	@Test
	void 실패_유저에_null_토큰을_넣으면_에러가_발생한다() {
		// given
		Member member = createMember();

		// when
		when(memberRepository.findById(0L)).thenReturn(Optional.of(member));

		// then
		assertThatThrownBy(() -> memberService.assignRefreshToken(0L, null))
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 성공_로그온한_유저가_참여한_대결_리스트를_전체_조회할_수_있다() {
		// given
		List<Post> posts = getPosts();
		List<Battle> battles = getBattles(posts);

		posts.forEach(post -> member.getPosts().add(post));

		when(principalService.getMemberByPrincipal(principal)).thenReturn(member);
		when(postRepository.findByMemberAndIsPossibleBattleIsTrue(member)).thenReturn(posts);

		// when
		MemberBattlesResponseDto result = memberService
			.getBattles(principal, null, null, null, null);

		// then
		assertThat(result).isEqualTo(getMemberBattlesResponseDto(battles));

		verify(principalService).getMemberByPrincipal(principal);
		verify(postRepository).findByMemberAndIsPossibleBattleIsTrue(member);
	}

	@Test
	void 성공_특정_유저가_참여한_대결_리스트를_전체_조회할_수_있다() {
		// given
		List<Post> posts = getPosts();
		List<Battle> battles = getBattles(posts);

		posts.forEach(post -> member.getPosts().add(post));

		when(postRepository.findByMemberAndIsPossibleBattleIsTrue(member)).thenReturn(posts);
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

		// when
		MemberBattlesResponseDto result = memberService
			.getBattles(principal, anyLong(), null, null, null);

		// then
		assertThat(result).isEqualTo(getMemberBattlesResponseDto(battles));
	}

	@Test
	void 성공_로그온한_유저가_참여한_대결_리스트를_대결상태_기준으로_조회할_수_있다() {
		// given
		List<Post> posts = getPosts();
		List<Battle> battles = getBattles(posts);

		posts.forEach(post -> member.getPosts().add(post));

		when(postRepository.findByMemberAndIsPossibleBattleIsTrue(member)).thenReturn(posts);
		when(principalService.getMemberByPrincipal(principal)).thenReturn(member);

		// when
		MemberBattlesResponseDto result = memberService
			.getBattles(principal, null, status, null, null);

		// then
		battles = battles.stream().filter(battle -> battle.getStatus().equals(status)).toList();
		assertThat(result).isEqualTo(getMemberBattlesResponseDto(battles));
	}

	@Test
	void 성공_특정_유저가_참여한_대결_리스트를_장르_기준으로_조회할_수_있다() {
		// given
		List<Post> posts = getPosts();
		List<Battle> battles = getBattles(posts);

		posts.forEach(post -> member.getPosts().add(post));

		when(postRepository.findByMemberAndIsPossibleBattleIsTrue(member)).thenReturn(posts);
		when(principalService.getMemberByPrincipal(principal)).thenReturn(member);

		// when
		MemberBattlesResponseDto result = memberService
			.getBattles(principal, null, null, genre, null);

		// then
		battles = battles.stream().filter(battle -> battle.getGenre().equals(genre)).toList();
		assertThat(result).isEqualTo(getMemberBattlesResponseDto(battles));
	}

	@Test
	void 성공_로그온한_유저가_참여한_대결_리스트를_원하는_길이만큼_조회할_수_있다() {
		// given
		List<Post> posts = getPosts();
		List<Battle> battles = getBattles(posts);

		int limit = 3;

		posts.forEach(post -> member.getPosts().add(post));

		when(postRepository.findByMemberAndIsPossibleBattleIsTrue(member)).thenReturn(posts);
		when(principalService.getMemberByPrincipal(principal)).thenReturn(member);

		// when
		MemberBattlesResponseDto result = memberService
			.getBattles(principal, null, null, null, limit);

		// then
		battles = new ArrayList<>(battles.subList(0, limit));
		assertThat(result).isEqualTo(getMemberBattlesResponseDto(battles));
	}

	private MemberBattlesResponseDto getMemberBattlesResponseDto(List<Battle> battles) {
		List<MemberBattleResponseDto> responses = battles.stream()
			.map(battle -> {
				return MemberBattleResponseDto.builder()
					.battleId(battle.getId())
					.genre(MemberBattleGenreVO.of(battle.getGenre()))
					.challenged(MemberBattlePostVO.of(battle.getChallengedPost().getPost()))
					.challenging(MemberBattlePostVO.of(battle.getChallengingPost().getPost()))
					.battleStatus(battle.getStatus())
					.build();
			})
			.toList();

		return MemberBattlesResponseDto.of(responses);
	}

	private List<Battle> getBattles(List<Post> posts) {
		return posts.stream().map(this::createBattle).toList();
	}

	private Battle createBattle(Post post) {
		Battle battle = Battle.builder()
			.genre(Genre.K_POP)
			.status(BattleStatus.PROGRESS)
			.challengedPost(post)
			.challengingPost(createPost(createMember()))
			.build();
		post.getChallengedBattles().add(battle);
		return battle;
	}

	private List<Post> getPosts() {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			posts.add(createPost(member));
		}
		return posts;
	}

	private Post createPost(Member member) {
		return Post.create(
			"musicId",
			"albumCoverUrl",
			"hype",
			"musicName",
			Genre.K_POP,
			"musicUrl",
			"recommend",
			true,
			member
		);
	}

	private List<Member> getMembers() {
		List<Member> members = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			members.add(createMember());
		}
		return members;
	}

	private Member createMember() {
		return Member.builder()
			.profileImageUrl("profile")
			.nickname("name")
			.refreshToken("token")
			.socialType(Social.GOOGLE)
			.socialId("socialId")
			.build();
	}

}
