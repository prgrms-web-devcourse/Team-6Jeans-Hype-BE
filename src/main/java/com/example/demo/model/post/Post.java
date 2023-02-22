package com.example.demo.model.post;

import static com.google.common.base.Preconditions.*;

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
	String content;

	private boolean isPossibleBattle;

	@Min(value = 0)
	private int likeCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToMany(mappedBy = "post")
	private List<Like> likes;

	@OneToMany(mappedBy = "challengedPost.post")
	private List<Battle> challengedBattles = new ArrayList<>();

	@OneToMany(mappedBy = "challengingPost.post")
	private List<Battle> challengingBattles = new ArrayList<>();

	public Post(String musicId, String albumCoverUrl, String singer, String title, Genre genre, String musicUrl,
		String content, int likeCount) {
		checkArgument(likeCount >= 0, "좋아요 개수가 음수일 수 없습니다.", likeCount);

		this.music = new Music(musicId, albumCoverUrl, singer, title, genre, musicUrl);
		this.content = content;
		this.likeCount = likeCount;
	}

}
