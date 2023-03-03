package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.Vote;
import com.example.demo.model.member.Member;

public interface VoteRepository extends JpaRepository<Vote, Long> {

	Optional<Vote> findByBattleAndVoter(Battle battle, Member voter);
}
