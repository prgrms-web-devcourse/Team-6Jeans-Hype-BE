package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.Vote;
import com.example.demo.model.member.Member;

public interface VoteRepository extends JpaRepository<Vote, Long> {

	List<Vote> findAllByVoterId(Long voterId);

	boolean existsByBattleAndVoter(Battle battle, Member voter);
}
