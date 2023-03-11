package com.example.demo.model.post;

import static com.google.common.base.Preconditions.*;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.demo.model.BaseEntity;
import com.example.demo.model.member.Member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "likes")
public class Like extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public Like(Post post, Member member) {
		checkArgument(Objects.nonNull(post), "게시글이 Null일 수 없습니다.");
		checkArgument(Objects.nonNull(member), "유저가 Null일 수 없습니다.");

		setPost(post);
		setMember(member);
	}

	public void setMember(Member member) {
		if (Objects.nonNull(this.member)) {
			this.member.getLikes().remove(this);
		}
		this.member = member;
		this.member.getLikes().add(this);
	}

	public void setPost(Post post) {
		if (Objects.nonNull(this.post)) {
			this.post.getLikes().remove(this);
		}
		this.post = post;
		this.post.getLikes().add(this);
	}
}
