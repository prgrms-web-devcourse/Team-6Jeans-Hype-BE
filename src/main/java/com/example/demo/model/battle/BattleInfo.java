package com.example.demo.model.battle;

import static com.google.common.base.Preconditions.*;

import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;

import com.example.demo.common.ExceptionMessage;
import com.example.demo.model.post.Post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class BattleInfo {

	@Min(value = 0)
	private int voteCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	private Post post;

	BattleInfo(Post post) {
		checkArgument(Objects.nonNull(post), "POST " + ExceptionMessage.OBJECT_NOT_NULL.getMessage());
		this.post = post;
		this.voteCount = 0;
	}
}
