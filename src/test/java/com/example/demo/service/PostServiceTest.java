package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@InjectMocks
	private PostService postService;
	@Mock
	private PostRepository postRepository;
	@Mock
	private MemberRepository memberRepository;

	private final String musicId = "musicId";
	private final String musicName = "musicName";
	private final String musicUrl = "musicUrl";
	private final String albumCoverUrl = "albumCoverUrl";
	private final Genre genre = Genre.DANCE;
	private final String singer = "hype";
	private final boolean isPossibleBattle = true;

	@Test
	void 성공_음악_공유_게시글을_등록할_수_있다() {
		// given
		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.musicId(musicId)
			.musicName(musicName)
			.musicUrl(musicUrl)
			.albumCoverUrl(albumCoverUrl)
			.genre(genre)
			.singer(singer)
			.isBattlePossible(isPossibleBattle)
			.build();

		Member member = createMember();
		Post post = postCreateRequestDto.toEntity(member);

		when(postRepository.save(any())).thenReturn(post);

		// when
		Long postId = postService.createPost(postCreateRequestDto);

		// then
		assertThat(postId).isEqualTo(post.getId());

		verify(postRepository).save(any());
	}

	private Member createMember() {

		return Member.builder()
			.profileImageUrl("profile")
			.nickname("name")
			.countOfChallengeTicket(5)
			.ranking(1)
			.victoryPoint(10)
			.victoryCount(10)
			.refreshToken("token")
			.socialType(Social.GOOGLE)
			.socialId("socialId")
			.build();
	}

}