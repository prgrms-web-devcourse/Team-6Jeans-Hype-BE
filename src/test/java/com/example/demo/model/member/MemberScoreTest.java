package com.example.demo.model.member;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberScoreTest {

	@ParameterizedTest
	@ValueSource(ints = -10)
	void 실패_음수인_포인트는_더할_수_없다(int value) {
		MemberScore memberScore = createMember().getMemberScore();
		assertThatThrownBy(() -> memberScore.plusPoint(value))
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@ValueSource(ints = -10)
	void 실패_음수인_랭킹은_업데이트할_수_없다(int value) {
		MemberScore memberScore = createMember().getMemberScore();
		assertThatThrownBy(() -> memberScore.updateRanking(value))
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	private Member createMember() {
		return Member.builder()
			.profileImageUrl("profile")
			.nickname("name")
			.refreshToken("token")
			.socialType(Social.GOOGLE)
			.socialId("socialId")
			.build();
	}

}
