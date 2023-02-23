package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.dto.post.PostFindResponseDto;
import com.example.demo.dto.post.PostsFindResponseDto;
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
	private final String content = "recommend";
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

	@Test
	void 성공_음악_공유_게시글을_모두_조회할_수_있다() {
		// given
		List<Post> testPosts = getPosts();
		PostsFindResponseDto expected = getPostsDto(testPosts);

		when(postRepository.findAll()).thenReturn(testPosts);

		// when
		PostsFindResponseDto posts = postService.findAllPosts(null, null);

		// then
		assertThat(posts).isEqualTo(expected);
	}

	@Test
	void 성공_음악_공유_게시글을_장르_기준으로_조회할_수_있다() {
		// given
		List<Post> testPosts = getPosts().stream()
			.filter(post -> post.getMusic().getGenre() == Genre.DANCE)
			.toList();
		PostsFindResponseDto expected = getPostsDto(testPosts);

		when(postRepository.findByMusic_Genre(Genre.DANCE)).thenReturn(testPosts);

		// when
		PostsFindResponseDto posts = postService.findAllPosts(Genre.DANCE, null);

		// then
		assertThat(posts).isEqualTo(expected);
	}

	@Test
	void 성공_음악_공유_게시글을_대결가능_기준으로_조회할_수_있다() {
		// given
		List<Post> testPosts = getPosts().stream()
			.filter(Post::isPossibleBattle)
			.toList();
		PostsFindResponseDto expected = getPostsDto(testPosts);

		when(postRepository.findByIsPossibleBattle(true)).thenReturn(testPosts);

		// when
		PostsFindResponseDto posts = postService.findAllPosts(null, true);

		// then
		assertThat(posts).isEqualTo(expected);
	}

	@Test
	void 성공_음악_공유_게시글을_장르와_대결가능_기준으로_조회할_수_있다() {
		// given
		List<Post> testPosts = getPosts().stream()
			.filter(post -> post.getMusic().getGenre() == Genre.POP && post.isPossibleBattle())
			.toList();
		PostsFindResponseDto expected = getPostsDto(testPosts);

		when(postRepository.findByMusic_GenreAndIsPossibleBattle(Genre.POP, true))
			.thenReturn(testPosts);

		// when
		PostsFindResponseDto posts = postService.findAllPosts(Genre.POP, true);

		// then
		assertThat(posts).isEqualTo(expected);
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

	private List<Post> getPosts() {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, Genre.DANCE, musicUrl,
				content, isPossibleBattle, createMember());
			posts.add(post);
		}
		for (int i = 0; i < 5; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, Genre.POP, musicUrl,
				content, isPossibleBattle, createMember());
			posts.add(post);
		}
		for (int i = 0; i < 5; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, Genre.POP, musicUrl,
				content, false, createMember());
			posts.add(post);
		}
		return posts;
	}

	private PostsFindResponseDto getPostsDto(List<Post> posts) {
		PostsFindResponseDto postsDto = PostsFindResponseDto.create();
		posts.forEach(post -> postsDto.posts().add(PostFindResponseDto.from(post)));
		return postsDto;
	}
}
