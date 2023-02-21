package com.example.demo.model.member;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberTest {

	private final int ranking = 1;
	private final int victoryPoint = 1000;
	private final int victoryCount = 10;
	private final String profileImageUrl = "profileImage";
	private final String nickname = "hype";
	private final String refreshToken = "token";
	private final Social socialType = Social.GOOGLE;
	private final String socialId = "socialId";

	@Test
	void 성공_Member_객체를_생성할_수_있다() {
		assertThat(createMember(profileImageUrl, nickname, refreshToken, socialType, socialId)).isExactlyInstanceOf(
			Member.class);
	}

	@Test
	void 실패_랭킹은_음수일_수_없다() {
		MemberScore memberScore = new MemberScore();
		int minusRanking = -1;

		assertThatThrownBy(() -> memberScore.update(minusRanking, victoryPoint, victoryCount)).isExactlyInstanceOf(
			IllegalArgumentException.class);
	}

	@Test
	void 실패_승리_포인트는_음수일_수_없다() {
		MemberScore memberScore = new MemberScore();
		int minusPoint = -1000;

		assertThatThrownBy(() -> memberScore.update(ranking, minusPoint, victoryCount)).isExactlyInstanceOf(
			IllegalArgumentException.class);
	}

	@Test
	void 실패_승리_횟수는_음수일_수_없다() {
		MemberScore memberScore = new MemberScore();
		int minusCount = -10;

		assertThatThrownBy(() -> memberScore.update(ranking, victoryPoint, minusCount)).isExactlyInstanceOf(
			IllegalArgumentException.class);
	}

	@Test
	void 성공_Member_에서_MemberScore_를_업데이트_할_수_있다() {
		Member member = createMember(profileImageUrl, nickname, refreshToken, socialType, socialId);

		assertThat(member.getMemberScore().getRanking()).isEqualTo(ranking);
		assertThat(member.getMemberScore().getVictoryPoint()).isEqualTo(victoryPoint);
		assertThat(member.getMemberScore().getVictoryCount()).isEqualTo(victoryCount);
	}

	@ParameterizedTest
	@NullSource
	void 실패_socialType_은_null_일_수_없다(Social value) {
		assertThatThrownBy(
			() -> createMember(profileImageUrl, nickname, refreshToken, value, socialId)).isExactlyInstanceOf(
			IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void 실패_socialId_는_null_이거나_공백일_수_없다(String value) {
		assertThatThrownBy(
			() -> createMember(profileImageUrl, nickname, refreshToken, socialType, value)).isExactlyInstanceOf(
			IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void 실패_profileImageUrl_은_null_이거나_공백일_수_없다(String value) {
		assertThatThrownBy(() -> createMember(value, nickname, refreshToken, socialType, socialId)).isExactlyInstanceOf(
			IllegalArgumentException.class);
	}

	@Test
	void 실패_profileImageUrl_의_길이는_2000자보다_클_수_없다() {
		StringBuilder url = new StringBuilder();
		while (url.length() <= 2000) {
			url.append("profile_image_url");
		}

		assertThatThrownBy(
			() -> createMember(url.toString(), nickname, refreshToken, socialType, socialId)).isExactlyInstanceOf(
			IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void 실패_nickname_은_null_이거나_공백일_수_없다(String value) {
		assertThatThrownBy(
			() -> createMember(profileImageUrl, value, refreshToken, socialType, socialId)).isExactlyInstanceOf(
			IllegalArgumentException.class);
	}

	@Test
	void 실패_nickname_길이가_24자보다_클_수_없다() {
		StringBuilder name = new StringBuilder();
		while (name.length() <= 24) {
			name.append("name");
		}

		assertThatThrownBy(() -> createMember(profileImageUrl, name.toString(), refreshToken, socialType,
			socialId)).isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullSource
	@EmptySource
	void 실패_refreshToken_이_null_이거나_공백일_수_없다(String value) {
		assertThatThrownBy(
			() -> createMember(profileImageUrl, nickname, value, socialType, socialId)).isExactlyInstanceOf(
			IllegalArgumentException.class);
	}

	private Member createMember(String profileImageUrl, String nickname, String refreshToken, Social socialType,
		String socialId) {

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
