package com.example.demo.model.member;

import javax.persistence.Embeddable;

import lombok.Getter;

@Getter
@Embeddable
public class MemberScore {
	private int ranking;
	private int victoryPoint;
	private int victoryCount;
}
