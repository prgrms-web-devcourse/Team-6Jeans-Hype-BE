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
import javax.validation.constraints.Min;

import com.example.demo.model.BaseEntity;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.member.Member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private Music music;

	@Lob
	private String content;

	private boolean isPossibleBattle;

	@Min(value = 0)
	private int likeCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToMany(mappedBy = "post")
	private final List<Like> likes = new ArrayList<>();

	@OneToMany(mappedBy = "challengedPost.post")
	private final List<Battle> challengedBattles = new ArrayList<>();

	@OneToMany(mappedBy = "challengingPost.post")
	private final List<Battle> challengingBattles = new ArrayList<>();

	@Builder(access = AccessLevel.PRIVATE)
	public Post(Music music, String content, boolean isPossibleBattle, int likeCount, Member member) {
		this.music = music;
		this.content = content;
		this.isPossibleBattle = isPossibleBattle;
		this.likeCount = likeCount;
		this.member = member;
	}

	public static Post create(String musicId, String albumCoverUrl, String singer, String title, Genre genre,
		String musicUrl, String content, boolean isPossibleBattle, Member member) {

		return Post.builder()
			.music(new Music(musicId, albumCoverUrl, singer, title, genre, musicUrl))
			.content(content)
			.isPossibleBattle(isPossibleBattle)
			.likeCount(0)
			.member(member)
			.build();
	}

}
