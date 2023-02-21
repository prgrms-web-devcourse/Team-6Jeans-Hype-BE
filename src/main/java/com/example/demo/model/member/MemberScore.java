package com.example.demo.model.member;

import static com.google.common.base.Preconditions.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.Min;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class MemberScore {

	@Min(value = 0)
	private int ranking;

	@Min(value = 0)
	private int victoryPoint;

	@Min(value = 0)
	private int victoryCount;

	protected void setRanking(int ranking) {
		checkArgument(ranking >= 0, "랭킹이 음수일 수 없습니다.", ranking);
		this.ranking = ranking;
	}

	protected void setVictoryPoint(int victoryPoint) {
		checkArgument(victoryPoint >= 0, "승리 포인트가 음수일 수 없습니다.", victoryPoint);
		this.victoryPoint = victoryCount;
	}

	protected void setVictoryCount(int victoryCount) {
		checkArgument(victoryCount >= 0, "승리 횟수가 음수일 수 없습니다.", victoryCount);
		this.victoryCount = victoryCount;
	}
}
