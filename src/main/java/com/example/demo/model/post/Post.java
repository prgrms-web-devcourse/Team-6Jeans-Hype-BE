package com.example.demo.model.post;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.example.demo.model.BaseEntity;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.member.Member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private Music music;

	@Lob
	String content;

	@NotNull
	private boolean isPossibleBattle;

	@NotNull
	private int likeCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	private Member member;

	@OneToMany(mappedBy = "post")
	private List<Like> likes;

	@OneToMany(mappedBy = "challengedPost")
	private List<Battle> chanllengedBattles = new ArrayList<>();

	@OneToMany(mappedBy = "challengingPost")
	private List<Battle> chanllengingBattles = new ArrayList<>();
}
