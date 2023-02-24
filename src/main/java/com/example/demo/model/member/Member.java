package com.example.demo.model.member;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.example.demo.model.BaseEntity;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.post.Like;
import com.example.demo.model.post.Post;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Length(max = 2000)
	private String profileImageUrl;

	@NotBlank
	@Length(min = 1, max = 24)
	private String nickname;

	@Min(value = 0)
	private int countOfChallengeTicket;

	@Embedded
	private MemberScore memberScore = new MemberScore();

	@NotBlank
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

	@Builder
	public Member(String profileImageUrl, String nickname, String refreshToken, Social socialType, String socialId) {

		validateMember(profileImageUrl, nickname, refreshToken);
		this.profileImageUrl = profileImageUrl;
		this.nickname = nickname;
		this.countOfChallengeTicket = 5;
		this.refreshToken = refreshToken;
		this.socialInfo = new SocialInfo(socialType, socialId);
	}

	public int getRanking() {
		return memberScore.getRanking();
	}

	public int getVictoryPoint() {
		return memberScore.getVictoryPoint();
	}

	public int getVictoryCount() {
		return memberScore.getVictoryCount();
	}

	public void updateMemberScore(int ranking, int victoryPoint, int victoryCount) {
		memberScore.update(ranking, victoryPoint, victoryCount);
	}

	private void validateMember(String profileImageUrl, String nickname, String refreshToken) {
		checkArgument(Objects.nonNull(profileImageUrl),
			"프로필 이미지 URL 이 Null 일 수 없습니다.", profileImageUrl);
		checkArgument(!profileImageUrl.isBlank(),
			"프로필 이미지 URL 이 공백일 수 없습니다.", profileImageUrl);
		checkArgument(profileImageUrl.length() <= 2000,
			"프로필 이미지 URL 이 2000자보다 더 길 수 없습니다.", profileImageUrl);

		checkArgument(Objects.nonNull(nickname),
			"닉네임이 Null 일 수 없습니다.", nickname);
		checkArgument(!nickname.isBlank(),
			"닉네임이 공백일 수 없습니다.", nickname);
		checkArgument(nickname.length() <= 24,
			"닉네임의 길이는 24보다 더 길 수 없습니다.", nickname);

		checkArgument(Objects.nonNull(refreshToken),
			"refresh token 이 Null 일 수 없습니다.", refreshToken);
		checkArgument(!refreshToken.isBlank(),
			"refresh token 이 공백일 수 없습니다.", refreshToken);
	}
	// TODO: 2023-02-23 member가 특정 배틀에 투표한적 있는지 확인하는 메소드(할수 없을 것 같기도)
}
