package com.example.demo.model.member;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.example.demo.model.BaseEntity;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.post.Like;
import com.example.demo.model.post.Post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String profileImageUrl;

	@NotNull
	private String nickname;

	private int countOfChallengeTicket;

	@Embedded
	private MemberScore memberScore;

	private String refreshToken;

	@Embedded
	private SocialInfo socialInfo;

	@OneToMany
	@JoinColumn(name = "member_id")
	private List<Battle> battles = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<Post> posts = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<Like> likes = new ArrayList<>();
}
