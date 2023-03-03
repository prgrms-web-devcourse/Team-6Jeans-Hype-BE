package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.battle.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

	List<Vote> findAllByVoterId(Long voterId);
}
