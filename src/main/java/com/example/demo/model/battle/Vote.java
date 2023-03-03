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

import com.example.demo.common.ExceptionMessage;
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
		checkArgument(Objects.nonNull(battle),
			String.format("battle %s", ExceptionMessage.OBJECT_NOT_NULL.getMessage()));
		checkArgument(Objects.nonNull(selectedPost),
			String.format("선택된 post %s", ExceptionMessage.OBJECT_NOT_NULL.getMessage()));
		checkArgument(Objects.nonNull(voter),
			String.format("투표자 %s", ExceptionMessage.OBJECT_NOT_NULL.getMessage()));

		// TODO: 2023-02-23 배틀이 해당 post를 가지고 있는지 검증
		// checkArgument(!battle.havePost(selectedPost), "selectedPost는 battle이 가지고 있는 post여야 합니다.");

		// TODO: 2023-02-23 포스트가 해당 배틀을 가지는지 검증
		// checkArgument(!selectedPost.haveBattle(battle), "selectedPost는 battle을 가지고 있어야 합니다.");

		// TODO: 2023-02-23 유저기 이전에 이미 vote 한적 있는 배틀인지 검증
		// checkArgument(voter.haveVotedBattle(battle), "해당 배틀에 이미 투표한 멤버입니다.");

		this.battle = battle;
		this.selectedPost = selectedPost;
		this.voter = voter;

	}

	public boolean checkVotedBattle(Long battleId) {
		return Objects.equals(battle.getId(), battleId);
	}
}
