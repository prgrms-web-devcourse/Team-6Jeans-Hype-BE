package com.example.demo.model.battle;

import static com.google.common.base.Preconditions.*;

import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.example.demo.common.ExceptionMessage;
import com.example.demo.model.BaseEntity;
import com.example.demo.model.post.Post;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Battle extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	@AttributeOverride(name = "voteCount", column = @Column(name = "challenged_vote_count"))
	@AssociationOverride(name = "post", joinColumns = @JoinColumn(name = "challenged_post_id"))
	private BattleInfo challengedPost;

	@Embedded
	@AttributeOverride(name = "voteCount", column = @Column(name = "challenging_vote_count"))
	@AssociationOverride(name = "post", joinColumns = @JoinColumn(name = "challenging_post_id"))
	private BattleInfo challengingPost;

	@Builder
	public Battle(Post challengedPost, Post challengingPost) {
		checkArgument(Objects.nonNull(challengedPost), "POST " + ExceptionMessage.OBJECT_NOT_NULL.getMessage());
		checkArgument(Objects.nonNull(challengingPost), "POST " + ExceptionMessage.OBJECT_NOT_NULL.getMessage());

		this.challengedPost = new BattleInfo(challengedPost);
		this.challengingPost = new BattleInfo(challengingPost);
	}

	// public boolean havePost(Post post) { - vouter에서 얘기해본 거에 대한 사안
	// 	return challengedPost.getPost().equals(post) || challengingPost.getPost().equals(post);
	// }
}
