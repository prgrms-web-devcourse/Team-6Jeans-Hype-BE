package com.example.demo.model.battle;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.member.Member;
import com.example.demo.model.post.Post;

@ExtendWith(MockitoExtension.class)
class VoteTest {
	@Mock
	Battle battleForSucess;
	@Mock
	Post postForSucess;
	@Mock
	Member memberForSucess;

	@Test
	public void 성공_vote생성_battle_post_member를_받아서_vote를_생성한다() {
		Vote vote = createVote(battleForSucess, postForSucess, memberForSucess);
		assertThat(vote.getBattle()).isEqualTo(battleForSucess);
		assertThat(vote.getSelectedPost()).isEqualTo(postForSucess);
		assertThat(vote.getVoter()).isEqualTo(memberForSucess);
	}

	@ParameterizedTest
	@MethodSource("testFailDataProvider")
	public void 실패_vote생성_battle_post_member중1개라도null이면_IllegalArgumentException이_발생한다(Battle battle, Post post,
		Member member) {
		assertThrows(IllegalArgumentException.class, () -> createVote(battle, post, member));
	}

	private static Stream<Arguments> testFailDataProvider() {
		Battle battle = mock(Battle.class);
		Post post = mock(Post.class);
		Member member = mock(Member.class);
		return Stream.of(
			Arguments.arguments(null, post, member),
			Arguments.arguments(battle, null, member),
			Arguments.arguments(battle, post, null)
		);
	}

	private Vote createVote(Battle battle, Post selectedPost, Member voter) {
		return Vote.builder().battle(battle).selectedPost(selectedPost).voter(voter).build();
	}
}
