package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Post {

	@Id
	@GeneratedValue
	Long postId;

	@NotNull
	Boolean isPossibleBattle;

	@NotNull
	Integer likeCount;

	@ManyToOne(fetch = FetchType.LAZY)
	Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	Music music;

	@OneToMany(mappedBy = "ownerPost")
	List<Battle> ownerBattles = new ArrayList<>();

	@OneToMany(mappedBy = "oppositePost")
	List<Battle> oppositeBattles = new ArrayList<>();
}
