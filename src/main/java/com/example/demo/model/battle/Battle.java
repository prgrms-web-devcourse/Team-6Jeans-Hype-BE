package com.example.demo.model.battle;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.example.demo.model.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
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
}
