package com.example.demo.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.battle.BattleDetailsListResponseDto;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.battle.Vote;
import com.example.demo.model.member.Member;
import com.example.demo.repository.BattleRepository;
import com.example.demo.repository.VoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleService {

	private final PrincipalService principalService;
	private final BattleRepository battleRepository;
	private final VoteRepository voteRepository;

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

