package com.example.demo.model.battle;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;

@ExtendWith(MockitoExtension.class)
class BattleInfoTest {

	@ParameterizedTest
	@ValueSource(ints = -10)
	void 실패_음수인_투표_수는_더할_수_없다(int value) {
		BattleInfo battleInfo = new BattleInfo(createPost());
		assertThatThrownBy(() -> battleInfo.plusVoteCount(value))
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	private Post createPost() {
		return Post.create(
			"musicId",
			"albumCoverUrl",
			"hype",
			"musicName",
			Genre.K_POP,
			"musicUrl",
			"recommend",
			true,
			createMember()
		);
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
