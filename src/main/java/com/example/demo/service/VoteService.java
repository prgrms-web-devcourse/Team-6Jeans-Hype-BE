package com.example.demo.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.example.demo.common.ExceptionMessage;
import com.example.demo.common.aop.DistributedLock;
import com.example.demo.dto.vote.VoteResultResponseDto;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleVotedResult;
import com.example.demo.model.battle.Vote;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Post;
import com.example.demo.repository.BattleRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.VoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoteService {

	private final BattleRepository battleRepository;
	private final PostRepository postRepository;
	private final VoteRepository voteRepository;

	@DistributedLock
	public VoteResultResponseDto voteBattle(Member member, Long battleId, Long postId) {
		Battle battle = battleRepository.findById(battleId)
			.orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_FOUND_BATTLE.getMessage()));
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.NOT_FOUND_POST.getMessage()));

		if (voteRepository.existsByBattleAndVoter(battle, member)) {
			throw new IllegalStateException(ExceptionMessage.DUPLICATED_USER_VOTE.getMessage());
		}

		Vote vote = Vote.builder()
			.battle(battle)
			.selectedPost(post)
			.voter(member)
			.build();

		voteRepository.save(vote);
		BattleVotedResult battleVotedResult = battle.vote(postId);

		return new VoteResultResponseDto(
			post.getMusic().getTitle(),
			post.getMusic().getAlbumCoverUrl(),
			battleVotedResult.selectedPostVoteCnt(),
			battleVotedResult.oppositePostVoteCnt()
		);
	}
}
