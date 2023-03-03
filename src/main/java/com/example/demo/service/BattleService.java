package com.example.demo.service;

import static com.example.demo.common.ExceptionMessage.*;

import java.security.Principal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.battle.BattleCreateRequestDto;
import com.example.demo.dto.battle.BattleDetailsListResponseDto;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.battle.Vote;
import com.example.demo.repository.VoteRepository;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.BattleRepository;
import com.example.demo.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleService {

	private final PrincipalService principalService;
	private final BattleRepository battleRepository;
	private final VoteRepository voteRepository;
	private final PostRepository postRepository;

	@Transactional
	public Long createBattle(Principal principal, BattleCreateRequestDto battleCreateRequestDto) {
		long challengedPostId = battleCreateRequestDto.challengedPostId();
		long challengingPostId = battleCreateRequestDto.challengingPostId();

		Post challengedPost = postRepository.findPostByIdAndIsPossibleBattle(challengedPostId, true)
			.orElseThrow(() -> new EntityNotFoundException(
				MessageFormat.format("postId {0}: {1}",
					challengedPostId, CANNOT_MAKE_BATTLE_WRONG_POST_ID.getMessage()
				)
			));

		Post challengingPost = postRepository.findPostByIdAndIsPossibleBattle(challengingPostId, true)
			.orElseThrow(() -> new EntityNotFoundException(
				MessageFormat.format("postId{0}: {1}",
					challengingPostId, CANNOT_MAKE_BATTLE_WRONG_POST_ID.getMessage()
				)
			));

		Member memberByPrincipal = principalService.getMemberByPrincipal(principal);

		validMemberHasChallengingPost(challengingPost, memberByPrincipal);
		validMemberHasNotChallengedPost(challengedPost, memberByPrincipal);
		validMemberChallengeTicket(memberByPrincipal);
		Genre targetGenre = validGenre(challengingPost, challengedPost);
		validSameMusic(challengingPost, challengedPost);
		Battle newBattle = Battle.builder()
			.challengingPost(challengingPost)
			.challengedPost(challengedPost)
			.genre(targetGenre)
			.status(BattleStatus.PROGRESS)
			.build();
		battleRepository.save(newBattle);
		memberByPrincipal.subtractCountOfChallengeTicket();
		return newBattle.getId();
	}

	private void validSameMusic(Post challengingPost, Post challengedPost) {
		String challenedPostMusicId = challengedPost.getMusic().getMusicId();
		String challengingPostMusicId = challengingPost.getMusic().getMusicId();
		if (challengingPostMusicId.equals(challenedPostMusicId)) {
			throw new IllegalArgumentException(CANNOT_MAKE_BATTLE_SAME_MUSIC.getMessage());
		}
	}

	private Genre validGenre(Post challengingPost, Post challengedPost) {
		if (!challengingPost.getMusic().getGenre().equals(challengedPost.getMusic().getGenre())) {
			throw new IllegalArgumentException(CANNOT_MAKE_BATTLE_DIFFERENT_GENRE.getMessage());
		} else {
			return challengedPost.getMusic().getGenre();
		}
	}

	private void validMemberChallengeTicket(Member memberByPrincipal) {
		if (memberByPrincipal.getCountOfChallengeTicket() == 0) {
			throw new IllegalStateException(CANNOT_MAKE_BATTLE_NOT_ENOUGH_CHALLENGE_TICKET.getMessage());
		}
	}

	private void validMemberHasNotChallengedPost(Post challengedPost, Member memberByPrincipal) {
		Long memberByPrincipalId = memberByPrincipal.getId();
		Long challengedMemberId = challengedPost.getMember().getId();
		if (memberByPrincipalId == challengedMemberId) {
			throw new IllegalArgumentException(CANNOT_MAKE_BATTLE_OWN_CHALLENED_POST.getMessage());
		}
	}

	private void validMemberHasChallengingPost(Post challengingPost, Member memberByPrincipal) {
		Long memberByPrincipalId = memberByPrincipal.getId();
		Long challengingMemberId = challengingPost.getMember().getId();
		if (memberByPrincipalId != challengingMemberId) {
			throw new IllegalArgumentException(CANNOT_MAKE_BATTLE_NOT_MEMBERS_POST.getMessage());
		}
	}

	@Transactional
	public void quitBattles() {
		findBattleProgress().forEach(Battle::quitBattle);
	}

	@Transactional
	public void updateWinnerPoint(int perm) {
		findBattlesEndWithinPerm(perm).forEach(Battle::updateWinnerPoint);
	}

	private List<Battle> findBattleProgress() {
		LocalDateTime endDate = LocalDateTime.now().minusDays(2);
		return battleRepository.findByStatusAndCreatedAtIsBefore(BattleStatus.PROGRESS, endDate);
	}

	private List<Battle> findBattlesEndWithinPerm(int perm) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime lastBattleDay = now.minusDays(perm - 1);
		return battleRepository.findByStatusAndUpdatedAtBetween(BattleStatus.END, lastBattleDay, now);
	}

	public BattleDetailsListResponseDto getBattleDetailsListInProgress(Principal principal) {
		Member member = principalService.getMemberByPrincipal(principal);
		List<Battle> battleListInProgress = battleRepository.findAllByStatusEquals(BattleStatus.PROGRESS);
		List<Vote> votes = voteRepository.findAllByVoterId(member.getId());

		List<Battle> battleListInProgressNotVotedByMember = battleListInProgress.stream()
			.filter(battle -> votes.stream()
				.noneMatch(vote -> vote.hasBattle(battle.getId()))
			)
			.toList();
		return BattleDetailsListResponseDto.of(
			battleListInProgressNotVotedByMember
		);
	}
}

