package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.battle.Battle;
import com.example.demo.repository.BattleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleService {

	private final BattleRepository battleRepository;

	public List<Battle> findBattlesWithinPerm(int perm) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = now.minusDays(perm);
		return battleRepository.findByCreatedAtBetween(startDate, now);
	}

	//TODO : 대결 종료 후 상태 변경
}
