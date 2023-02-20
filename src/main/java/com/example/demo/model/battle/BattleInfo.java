package com.example.demo.model.battle;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.demo.model.post.Post;

import lombok.Getter;

@Getter
@Embeddable
public class BattleInfo {

	private int voteCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	private Post post;
}
