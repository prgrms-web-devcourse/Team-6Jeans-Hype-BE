package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Member {

	// TODO: Member에서 Battles를 가지고 있는게 맞을 지 -> 직관적으론 가지고 있는게 맞다고 느껴지는데 Battle을 짜다보면 Battle에서 Member가 필요 없음.
	// TODO: 포인트, 랭킹, 승수 이 부분이 애매함.

	@Id
	@GeneratedValue
	Long memberId;

	@NotNull
	String memberName;

	Integer ranking;
	Integer winningPoint;
	Integer winningCount;

	@OneToOne(fetch = FetchType.LAZY)
	Image image;

	@OneToMany(mappedBy = "member")
	List<Battle> battles = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	List<Post> posts = new ArrayList<>();
}
