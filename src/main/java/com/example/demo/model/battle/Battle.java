package com.example.demo.model.battle;

import static com.google.common.base.Preconditions.*;

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
		setChallengedPost(challengedPost);
		setChallengingPost(challengingPost);

		this.genre = genre;
		this.status = status;

	}

	private void setChallengedPost(Post challengedPost) {
		if (this.challengedPost != null) {
			this.challengedPost.getPost().getChallengedBattles().remove(this);
		}
		this.challengedPost = new BattleInfo(challengedPost);
		challengedPost.getChallengedBattles().add(this);
	}

	private void setChallengingPost(Post challengingPost) {
		if (this.challengingPost != null) {
			this.challengingPost.getPost().getChallengingBattles().remove(this);
		}
		this.challengingPost = new BattleInfo(challengingPost);
		challengingPost.getChallengingBattles().add(this);
	}

	public void plusVoteCount(BattleInfo battleInfo, int voteCount) {
		battleInfo.plusVoteCount(voteCount);
	}

	public void endBattle() {
		this.status = BattleStatus.END;
	}

	public void quitBattle() {
		endBattle();
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
		winner.ifPresent(Member::plusVictoryCount);
	}

	public Optional<Member> getWinner() {
		Optional<Post> wonPost = getWonPost();
		return wonPost.map(Post::getMember);
	}

	public Optional<Post> getWonPost() {
		int diff = challengedPost.getVoteCount() - challengingPost.getVoteCount();

		if (diff > 0) {
			return Optional.of(challengedPost.getPost());
		} else if (diff < 0) {
			return Optional.of(challengingPost.getPost());
		}

		return Optional.empty();
	}

	public int getPoint() {
		return Math.abs(challengedPost.getVoteCount() - challengingPost.getVoteCount());
	}

	public BattleVotedResult vote(Long postId) {
		Long challengedPostId = challengedPost.getPost().getId();
		Long challengingPostId = challengingPost.getPost().getId();

		if (Objects.equals(postId, challengingPostId)) {
			challengingPost.plusVoteCount();
			return voteResult(challengingPost.getVoteCount(), challengedPost.getVoteCount());
		} else if (Objects.equals(postId, challengedPostId)) {
			challengedPost.plusVoteCount();
			return voteResult(challengedPost.getVoteCount(), challengingPost.getVoteCount());
		} else {
			throw new IllegalArgumentException(ExceptionMessage.POST_NOT_CONTAIN_BATTLE.getMessage());
		}
	}

	private BattleVotedResult voteResult(int selectedPostVoteCnt, int oppositePostVoteCnt) {
		return new BattleVotedResult(selectedPostVoteCnt, oppositePostVoteCnt);
	}

	public Boolean isProgress() {
		return switch (this.status) {
			case PROGRESS -> true;
			case END -> false;
		};
	}

}
