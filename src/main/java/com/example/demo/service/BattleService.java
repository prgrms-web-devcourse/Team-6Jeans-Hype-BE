package com.example.demo.service;

import static com.example.demo.common.ExceptionMessage.*;

import java.security.Principal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.battle.BattleCreateRequestDto;
import com.example.demo.dto.battle.BattleDetailByIdResponseDto;
import com.example.demo.dto.battle.BattleDetailsListResponseDto;
import com.example.demo.dto.battle.BattleDetailsResponseDto;
import com.example.demo.dto.battle.BattlesResponseDto;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.battle.Vote;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.BattleRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.repository.custom.BattleRepositoryCustom;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleService {

	private final PrincipalService principalService;
	private final BattleRepository battleRepository;
	private final VoteRepository voteRepository;
	private final PostRepository postRepository;
	private final BattleRepositoryCustom battleRepositoryCustom;

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
		validAlreadyExsistBattles(challengingPost, challengedPost);

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

	private void validAlreadyExsistBattles(Post challengingPost, Post challengedPost) {
		boolean isAlreadyExsistBattle = false;
		boolean isAlreadyExsistReverseBattle = false;
		isAlreadyExsistBattle =
			battleRepository.existsByChallengedPost_PostAndChallengingPost_PostAndStatus(
				challengedPost,
				challengingPost,
				BattleStatus.PROGRESS
			);

		isAlreadyExsistReverseBattle =
			battleRepository.existsByChallengedPost_PostAndChallengingPost_PostAndStatus(
				challengingPost,
				challengedPost,
				BattleStatus.PROGRESS
			);
		if (isAlreadyExsistBattle || isAlreadyExsistReverseBattle) {
			throw new IllegalArgumentException(CANNOT_MAKE_BATTLE_ALREADY_EXIST_PROGRESS_BATTLE.getMessage());
		}
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
	public void updateWinnerPoint(int term) {
		findBattlesEndWithinPerm(term).forEach(Battle::updateWinnerPoint);
	}

	private List<Battle> findBattleProgress() {
		LocalDateTime now = LocalDateTime.now();
		return battleRepositoryCustom.findByStatusAndCreatedAtIsBefore(BattleStatus.PROGRESS, now);
	}

	private List<Battle> findBattlesEndWithinPerm(int term) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime lastBattleDay = now.minusDays(term);
		return battleRepositoryCustom.findByStatusAndUpdatedAtBetween(BattleStatus.END, lastBattleDay, now);
	}

	public BattleDetailsListResponseDto getBattleDetailsListInProgress(Principal principal) {
		Member member = principalService.getMemberByPrincipal(principal);
		List<Battle> battleListInProgress = battleRepository.findAllByStatusOrderByCreatedAtDesc(BattleStatus.PROGRESS);
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

	public BattlesResponseDto getBattles() {
		List<Battle> allBattles = battleRepository.findAllByOrderByCreatedAtDesc();
		return BattlesResponseDto.of(allBattles);
	}

	public BattlesResponseDto getBattles(BattleStatus battleStatus) {
		List<Battle> allByStatusEquals = battleRepository.findAllByStatusOrderByCreatedAtDesc(battleStatus);
		return BattlesResponseDto.of(allByStatusEquals);
	}

	public BattlesResponseDto getBattles(Genre genre) {
		List<Battle> battles = battleRepository.findAllByGenreOrderByCreatedAtDesc(genre);
		return BattlesResponseDto.of(battles);
	}

	public BattlesResponseDto getBattles(BattleStatus battleStatus, Genre genre) {
		List<Battle> battles = battleRepository.findAllByStatusAndGenreEqualsOrderByCreatedAtDesc(battleStatus, genre);
		return BattlesResponseDto.of(battles);
	}

	public BattleDetailByIdResponseDto getBattleDetailById(Principal principal, Long battleId) {
		Member member = principalService.getMemberByPrincipal(principal);
		Battle battle = battleRepository.findById(battleId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_BATTLE.getMessage()));
		Optional<Long> selectedPostId = voteRepository.findByBattleAndVoter(battle, member)//투표한 적 있다면?
			.map(element -> Optional.of(element.getSelectedPost().getId()))
			.orElse(Optional.empty());
		if (selectedPostId.isEmpty()) {
			//투표를 안했다
			return BattleDetailByIdResponseDto.ofNotVoted(battle);
		} else {
			//투표를 했다.
			return BattleDetailByIdResponseDto.ofVoted(battle, selectedPostId.get());
		}
	}

	public BattleDetailsResponseDto getRandomBattleDetail() {
		List<Battle> progressBattles = battleRepository.findByStatus(BattleStatus.PROGRESS);
		if (progressBattles.size() < 1) {
			throw new IllegalArgumentException(NOT_PROGRESS_BATTLE.getMessage());
		}

		int randomIndex = new Random().nextInt(progressBattles.size());
		return BattleDetailsResponseDto.of(progressBattles.get(randomIndex));
	}
}

