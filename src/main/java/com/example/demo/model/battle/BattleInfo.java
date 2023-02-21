package com.example.demo.model.battle;

import static com.google.common.base.Preconditions.*;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;

import com.example.demo.model.post.Post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleInfo {

	@Min(value = 0)
	private int voteCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	private Post post;

	protected BattleInfo(int voteCount) {
		checkArgument(voteCount >= 0, "득표수가 음수일 수 없습니다.", voteCount);
		this.voteCount = voteCount;
	}
}
