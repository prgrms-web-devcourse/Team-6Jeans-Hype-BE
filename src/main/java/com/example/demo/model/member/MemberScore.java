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
public class MemberScore {

	@Min(value = 0)
	private int ranking;

	@Min(value = 0)
	private int victoryPoint;

	@Min(value = 0)
	private int victoryCount;

	void update(int ranking, int victoryPoint, int victoryCount) {
		validateMemberScore(ranking, victoryPoint, victoryCount);
		this.ranking = ranking;
		this.victoryPoint = victoryPoint;
		this.victoryCount = victoryCount;
	}

	private void validateMemberScore(int ranking, int victoryPoint, int victoryCount) {
		checkArgument(ranking >= 0, "랭킹이 음수일 수 없습니다.", ranking);
		checkArgument(victoryPoint >= 0, "승리 포인트가 음수일 수 없습니다.", victoryPoint);
		checkArgument(victoryCount >= 0, "승리 횟수가 음수일 수 없습니다.", victoryCount);
	}

	void plusVictoryCount() {
		this.victoryCount += 1;
	}

	void resetRankingAndPoint() {
		this.ranking = 0;
		this.victoryPoint = 0;
	}

	void plusPoint(int point) {
		checkArgument(point >= 0, "포인트가 음수일 수 없습니다.", point);
		this.victoryPoint += point;
	}

	void updateRanking(int ranking) {
		checkArgument(ranking >= 0, "랭킹이 음수일 수 없습니다.", ranking);
		this.ranking = ranking;
	}
}
