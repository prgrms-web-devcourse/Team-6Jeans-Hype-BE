package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Battle {

	// TODO: Battle에서 두 개의 Post 정보를 가져야함. -> Post와의 관계가 다대일이 되는게 맞을지, Post가 다른 필드로 2번 오는게 맞을지

	@Id
	@GeneratedValue
	Long battleId;

	@NotNull
	Integer ownerVoteCount;

	@NotNull
	Integer oppositeVoteCount;

	@ManyToOne(fetch = FetchType.LAZY)
	Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	Post ownerPost;

	@ManyToOne(fetch = FetchType.LAZY)
	Post oppositePost;
}
