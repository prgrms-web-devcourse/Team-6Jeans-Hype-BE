package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;

@Repository
public interface BattleRepository extends JpaRepository<Battle, Long> {
	List<Battle> findByStatusAndCreatedAtIsBefore(BattleStatus status, LocalDateTime endDate);

	List<Battle> findAllByStatusEquals(BattleStatus progressStatus);

	List<Battle> findByStatusAndUpdatedAtBetween(BattleStatus status, LocalDateTime startDate, LocalDateTime endDate);
}
