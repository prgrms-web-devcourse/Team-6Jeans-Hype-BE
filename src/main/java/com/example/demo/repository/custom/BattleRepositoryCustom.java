package com.example.demo.repository.custom;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.battle.QBattle;
import com.example.demo.model.member.QMember;
import com.example.demo.model.post.QPost;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BattleRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QBattle battle = QBattle.battle;
	private final QPost challengedPost = QPost.post;
	private final QPost challengingPost = QPost.post;
	private final QMember challengedMember = QMember.member;
	private final QMember challengingMember = QMember.member;

	public List<Battle> findByStatusAndCreatedAtIsBefore(BattleStatus status, LocalDateTime now) {
		return queryFactory
			.selectFrom(battle)
			.join(battle.challengedPost.post, challengedPost).fetchJoin()
			.join(battle.challengingPost.post, challengingPost).fetchJoin()
			.join(challengedPost.member, challengedMember).fetchJoin()
			.join(challengingPost.member, challengingMember).fetchJoin()
			.where(battle.status.eq(status), battle.createdAt.before(now))
			.fetch();
	}

	public List<Battle> findByStatusAndUpdatedAtBetween(BattleStatus status, LocalDateTime startDate,
		LocalDateTime endDate) {
		return queryFactory
			.selectFrom(battle)
			.join(battle.challengedPost.post, challengedPost).fetchJoin()
			.join(battle.challengingPost.post, challengingPost).fetchJoin()
			.join(challengedPost.member, challengedMember).fetchJoin()
			.join(challengingPost.member, challengingMember).fetchJoin()
			.where(battle.status.eq(status), battle.updatedAt.between(startDate, endDate))
			.fetch();
	}
}
