package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;

public interface BattleRepository extends JpaRepository<Battle, Long> {

	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT b FROM Battle b WHERE b.id = :battleId")
	Optional<Battle> findByIdPessimisticLock(@Param("battleId") Long battleId);

	List<Battle> findByStatusAndCreatedAtIsBefore(BattleStatus status, LocalDateTime endDate);

	List<Battle> findAllByStatusEquals(BattleStatus progressStatus);

	List<Battle> findByStatusAndUpdatedAtBetween(BattleStatus status, LocalDateTime startDate, LocalDateTime endDate);
}
