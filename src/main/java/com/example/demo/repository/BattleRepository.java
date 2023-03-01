package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.battle.Battle;

public interface BattleRepository extends JpaRepository<Battle, Long> {
}
