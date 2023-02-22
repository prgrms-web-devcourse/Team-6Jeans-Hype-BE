package com.example.demo.model.battle;

import static com.google.common.base.Preconditions.*;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.demo.model.BaseEntity;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Post;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "battle_id")
	private Battle battle;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post selectedPost;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member voter;

	@Builder
	public Vote(Battle battle, Post selectedPost, Member voter) {
		checkArgument(Objects.nonNull(battle), "");
		checkArgument(Objects.nonNull(selectedPost), "");
		checkArgument(Objects.nonNull(voter), "");

		// TODO: 2023-02-22 아래의 함수들이 있어야 하지 않을까 싶습니다. 근데 저걸 할라면 equals and hashcode의 override가 필요할 것 같아요
		// checkArgument(!battle.havePost(selectedPost), "selectedPost는 battle이 가지고 있는 post여야 합니다.");
		// 배틀이 해당 post를 가지고 있는지 검증

		// checkArgument(!selectedPost.haveBattle(battle), "selectedPost는 battle을 가지고 있어야 합니다.");
		// 포스트가 해당 배틀을 가지는지 검증

		// checkArgument(voter.haveBattle(battle), "멤버는 배틀을 가지고 있으면 안됩니다.");
		// 멤버가 이전에 이미 투표한 적이 있는건 아닌지 검증

		this.battle = battle;
		this.selectedPost = selectedPost;
		this.voter = voter;

	}
}
