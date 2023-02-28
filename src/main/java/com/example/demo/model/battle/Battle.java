package com.example.demo.model.battle;

import static com.google.common.base.Preconditions.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.validation.constraints.NotNull;

import com.example.demo.common.ExceptionMessage;
import com.example.demo.model.BaseEntity;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
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

	@NotNull
	@Enumerated(value = EnumType.STRING)
	private Genre genre;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "status")
	private BattleStatus status;

	@Embedded
	@AttributeOverride(name = "voteCount", column = @Column(name = "challenged_vote_count"))
	@AssociationOverride(name = "post", joinColumns = @JoinColumn(name = "challenged_post_id"))
	private BattleInfo challengedPost;

	@Embedded
	@AttributeOverride(name = "voteCount", column = @Column(name = "challenging_vote_count"))
	@AssociationOverride(name = "post", joinColumns = @JoinColumn(name = "challenging_post_id"))
	private BattleInfo challengingPost;

	@Builder
	public Battle(Genre genre, BattleStatus status, Post challengedPost, Post challengingPost) {
		String errorMessageForNullPost = String.format("POST %s", ExceptionMessage.OBJECT_NOT_NULL.getMessage());
		checkArgument(Objects.nonNull(genre), errorMessageForNullPost);
		checkArgument(Objects.nonNull(status), errorMessageForNullPost);
		checkArgument(Objects.nonNull(challengedPost), errorMessageForNullPost);
		checkArgument(Objects.nonNull(challengingPost), errorMessageForNullPost);

		this.genre = genre;
		this.status = status;
		this.challengedPost = new BattleInfo(challengedPost);
		this.challengingPost = new BattleInfo(challengingPost);
	}

	public void plusVoteCount(BattleInfo battleInfo, int voteCount) {
		battleInfo.plusVoteCount(voteCount);
	}

	public void quitBattle() {
		this.status = BattleStatus.END;
		this.updateTime(LocalDateTime.now());
		updateCountOfWinner();
	}

	public void updateWinnerPoint() {
		Optional<Member> winner = getWinner();
		if (winner.isPresent()) {
			int point = getPoint();
			winner.get().plusPoint(point);
		}
	}

	private void updateCountOfWinner() {
		Optional<Member> winner = getWinner();
		winner.ifPresent(Member::plusCount);
	}

	private Optional<Member> getWinner() {
		int diff = challengedPost.getVoteCount() - challengingPost.getVoteCount();

		if (diff > 0) {
			return Optional.of(challengedPost.getPost().getMember());
		} else if (diff < 0) {
			return Optional.of(challengingPost.getPost().getMember());
		}

		return Optional.empty();
	}

	private int getPoint() {
		return Math.abs(challengedPost.getVoteCount() - challengingPost.getVoteCount());
	}

	// TODO: 2023-02-23 battle이 특정 Post를 가지고 있는지 검증하는 메소드
}
