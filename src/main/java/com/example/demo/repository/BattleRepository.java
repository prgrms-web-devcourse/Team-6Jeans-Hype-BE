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
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;

public interface BattleRepository extends JpaRepository<Battle, Long> {

	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT b FROM Battle b WHERE b.id = :battleId")
	Optional<Battle> findByIdPessimisticLock(@Param("battleId") Long battleId);

	List<Battle> findByStatusAndCreatedAtIsBefore(BattleStatus status, LocalDateTime endDate);

	List<Battle> findAllByStatusOrderByCreatedAtDesc(BattleStatus progressStatus);

	List<Battle> findByStatusAndUpdatedAtBetween(BattleStatus status, LocalDateTime startDate, LocalDateTime endDate);

	List<Battle> findAllByOrderByCreatedAtDesc();

	List<Battle> findAllByGenreOrderByCreatedAtDesc(Genre genre);

	List<Battle> findAllByStatusAndGenreEqualsOrderByCreatedAtDesc(BattleStatus status, Genre genre);

	boolean existsByChallengedPost_PostAndChallengingPost_PostAndStatus(
		Post challengedPost, Post challengingPost, BattleStatus battleStatus
	);

	List<Battle> findByStatus(BattleStatus status);
}
