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
}
