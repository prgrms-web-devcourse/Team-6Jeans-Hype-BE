package com.example.demo.model.member;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberTest {

	@Test
	void 랭킹은_음수일수_없다() {
		// given
		int minusRanking = -1;

		MemberScore memberScore = new MemberScore();

		// when then
		assertThatThrownBy(() -> memberScore.update(minusRanking, 1000, 10))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("랭킹이 음수일 수 없습니다. [" + minusRanking + "]");
	}

	@Test
	void 승리포인트는_음수일수_없다() {
		// given
		int minusPoint = -1000;

		MemberScore memberScore = new MemberScore();

		// when then
		assertThatThrownBy(() -> memberScore.update(1, minusPoint, 10))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("승리 포인트가 음수일 수 없습니다. [" + minusPoint + "]");
	}

	@Test
	void 승리횟수는_음수일수_없다() {
		// given
		int minusCount = -10;

		MemberScore memberScore = new MemberScore();

		// when then
		assertThatThrownBy(() -> memberScore.update(1, 1000, minusCount))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("승리 횟수가 음수일 수 없습니다. [" + minusCount + "]");
	}

	@Test
	void Member_에서_MemberScore_를_업데이트할수있다() {
		// given
		int ranking = 1;
		int victoryPoint = 1000;
		int victoryCount = 10;

		// when
		Member member = createMember("url", "닉네임", ranking,
			victoryPoint, victoryCount, "token", Social.GOOGLE, "1234");

		// then
		assertThat(member.getMemberScore().getRanking()).isEqualTo(ranking);
		assertThat(member.getMemberScore().getVictoryPoint()).isEqualTo(victoryPoint);
		assertThat(member.getMemberScore().getVictoryCount()).isEqualTo(victoryCount);
	}

	@Test
	void socialType_은_null_일수없다() {
		// given
		Social socialType = null;

		// when then
		assertThatThrownBy(() -> createMember("url", "닉네임",
			1, 1000, 10, "token", socialType, "1234"))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("소셜 타입은 Null 일 수 없습니다. [" + socialType + "]");
	}

	@Test
	void socialId_는_null_일수없다() {
		// given
		String socialId = null;

		// when then
		assertThatThrownBy(() -> createMember("url", "닉네임",
			1, 1000, 10, "token", Social.GOOGLE, socialId))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("소셜 ID가 Null 일 수 없습니다. [" + socialId + "]");
	}

	@Test
	void socialId_는_공백_일수없다() {
		// given
		String socialId = "";

		// when then
		assertThatThrownBy(() -> createMember("url", "닉네임",
			1, 1000, 10, "token", Social.GOOGLE, socialId))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("소셜 ID가 공백일 수 없습니다. [" + socialId + "]");
	}

	@Test
	void profileImageUrl_은_null_일수없다() {
		// given
		String profileImageUrl = null;

		// when then
		assertThatThrownBy(() -> createMember(profileImageUrl, "닉네임",
			1, 1000, 10, "token", Social.GOOGLE, "1234"))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("프로필 이미지 URL 이 Null 일 수 없습니다. [" + profileImageUrl + "]");
	}

	@Test
	void profileImageUrl_은_공백_일수없다() {
		// given
		String profileImageUrl = "";

		// when then
		assertThatThrownBy(() -> createMember(profileImageUrl, "닉네임",
			1, 1000, 10, "token", Social.GOOGLE, "1234"))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("프로필 이미지 URL 이 공백일 수 없습니다. [" + profileImageUrl + "]");
	}

	@Test
	void profileImageUrl_의_길이는2000자보다_클수없다() {
		// given
		StringBuilder profileImageUrl = new StringBuilder();
		while (profileImageUrl.length() <= 2000) {
			profileImageUrl.append("profile_image_url");
		}

		// when then
		assertThatThrownBy(() -> createMember(profileImageUrl.toString(), "닉네임",
			1, 1000, 10, "token", Social.GOOGLE, "1234"))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("프로필 이미지 URL 이 2000자보다 더 길 수 없습니다. [" + profileImageUrl + "]");
	}

	@Test
	void nickname_이_null_일수없다() {
		// given
		String nickname = null;

		// when then
		assertThatThrownBy(() -> createMember("profile", nickname,
			1, 1000, 10, "token", Social.GOOGLE, "1234"))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("닉네임이 Null 일 수 없습니다. [" + nickname + "]");
	}

	@Test
	void nickname_이_공백_일수없다() {
		// given
		String nickname = "";

		// when then
		assertThatThrownBy(() -> createMember("profile", nickname,
			1, 1000, 10, "token", Social.GOOGLE, "1234"))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("닉네임이 공백일 수 없습니다. [" + nickname + "]");
	}

	@Test
	void nickname_길이가_24자보다_클수없다() {
		// given
		StringBuilder nickname = new StringBuilder();
		while (nickname.length() <= 24) {
			nickname.append("name");
		}

		// when then
		assertThatThrownBy(() -> createMember("profile", nickname.toString(),
			1, 1000, 10, "token", Social.GOOGLE, "1234"))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("닉네임의 길이는 24보다 더 길 수 없습니다. [" + nickname + "]");
	}

	@Test
	void refreshToken_이_null_일수없다() {
		// given
		String refreshToken = null;

		// when then
		assertThatThrownBy(() -> createMember("profile", "nickname",
			1, 1000, 10, refreshToken, Social.GOOGLE, "1234"))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("refresh token 이 Null 일 수 없습니다. [" + refreshToken + "]");
	}

	@Test
	void refreshToken_이_공백_일수없다() {
		// given
		String refreshToken = "";

		// when then
		assertThatThrownBy(() -> createMember("profile", "nickname",
			1, 1000, 10, refreshToken, Social.GOOGLE, "1234"))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("refresh token 이 공백일 수 없습니다. [" + refreshToken + "]");
	}

	private Member createMember(String profileImageUrl, String nickname, int ranking,
		int victoryPoint, int victoryCount, String refreshToken, Social socialType, String socialId) {

		return Member.builder()
			.profileImageUrl(profileImageUrl)
			.nickname(nickname)
			.countOfChallengeTicket(5)
			.ranking(ranking)
			.victoryPoint(victoryPoint)
			.victoryCount(victoryCount)
			.refreshToken(refreshToken)
			.socialType(socialType)
			.socialId(socialId)
			.build();
	}

}
