package com.example.demo.model.battle;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;

@ExtendWith(MockitoExtension.class)
class BattleTest {
	@Mock
	private Post challengingPost;
	@Mock
	private Post challengedPost;

	@Test
	public void 성공_Battle생성_Post가_성공적으로들어오면_Battle이만들어진다() {
		Battle battle = createBattle(challengingPost, challengedPost);

		assertThat(battle.getChallengedPost().getVoteCount()).isEqualTo(0);
		assertThat(battle.getChallengingPost().getVoteCount()).isEqualTo(0);

	}

	@ParameterizedTest
	@MethodSource("testFailDataProviderForCreateBattle")
	public void 실패_Battle생성_Post가Null이면_IllegalArgumentException이_발생한다(Post challengingPost, Post challengedPost) {

		IllegalArgumentException exception = Assert.assertThrows(IllegalArgumentException.class, () ->
			createBattle(challengingPost, challengedPost)
		);
	}

	@ParameterizedTest
	@NullSource
	public void 실패_Battle생성_장르가_null인_경우_게시글을_생성할_수_없다(Genre nullGenre) {
		assertThatThrownBy(() -> {
			Battle.builder()
				.genre(nullGenre)
				.status(BattleStatus.PROGRESS)
				.challengedPost(challengedPost)
				.challengingPost(challengingPost)
				.build();
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullSource
	public void 실패_Battle생성_대결상태가_null인_경우_게시글을_생성할_수_없다(BattleStatus nullStatus) {
		assertThatThrownBy(() -> {
			Battle.builder()
				.genre(Genre.CLASSIC)
				.status(nullStatus)
				.challengedPost(challengedPost)
				.challengingPost(challengingPost)
				.build();
		})
			.isExactlyInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void 성공_대결에서_동점이면_빈_Optional을_반환할_수_있다() {
		Battle battle = createBattle(challengingPost, challengedPost);
		assertThat(battle.getWinner().isEmpty()).isEqualTo(true);
	}

	@Test
	public void 성공_대결에서_승리한_유저를_반환할_수_있다() {
		Battle battle = createBattle(createPost(createMember()), createPost(createMember()));
		battle.plusVoteCount(battle.getChallengingPost(), 10);
		assertThat(battle.getWinner().isPresent()).isEqualTo(true);
		assertThat(battle.getWinner().get()).isEqualTo(battle.getChallengingPost().getPost().getMember());
	}

	@Test
	public void 성공_대결에서_득표_수_차이를_반환할_수_있다() {
		Battle battle = createBattle(challengingPost, challengedPost);
		assertThat(battle.getPoint()).isEqualTo(0);
	}

	static Stream<Arguments> testFailDataProviderForCreateBattle() {
		Post challengingPost = mock(Post.class);
		Post challengedPost = mock(Post.class);
		return Stream.of(
			Arguments.arguments(null, challengedPost),
			Arguments.arguments(challengingPost, null),
			Arguments.arguments(null, null)
		);
	}

	private Battle createBattle(Post challengingPost, Post challengedPost) {
		return Battle.builder()
			.genre(Genre.CLASSIC)
			.status(BattleStatus.PROGRESS)
			.challengedPost(challengedPost)
			.challengingPost(challengingPost)
			.build();
	}

	private Post createPost(Member member) {
		return Post.create(
			"musicId",
			"albumCoverUrl",
			"hype",
			"musicName",
			Genre.CLASSIC,
			"musicUrl",
			"recommend",
			true,
			member
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
