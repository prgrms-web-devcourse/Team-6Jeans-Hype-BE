package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.post.PostBattleCandidateResponseDto;
import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.dto.post.PostDetailFindResponseDto;
import com.example.demo.dto.post.PostFindResponseDto;
import com.example.demo.dto.post.PostsBattleCandidateResponseDto;
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
	private final Member member = createMember();

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
			.content(content)
			.build();
		Post post = postCreateRequestDto.toEntity(member);

		when(postRepository.save(any())).thenReturn(post);

		// when
		Long postId = postService.createPost(member, postCreateRequestDto);

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

		verify(postRepository).findAll();
	}

	private List<Post> getPosts() {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, genre, musicUrl,
				content, isPossibleBattle, member);
			posts.add(post);
		}
		return posts;
	}

	@Test
	void 성공_음악_공유_게시글을_장르_기준으로_조회할_수_있다() {
		// given
		List<Post> testPosts = getPosts(genre);
		PostsFindResponseDto expected = getPostsDto(testPosts);

		when(postRepository.findByMusic_Genre(genre)).thenReturn(testPosts);

		// when
		PostsFindResponseDto posts = postService.findAllPosts(genre, null);

		// then
		assertThat(posts).isEqualTo(expected);

		verify(postRepository).findByMusic_Genre(genre);
	}

	private List<Post> getPosts(Genre genre) {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, genre, musicUrl,
				content, isPossibleBattle, member);
			posts.add(post);
		}
		return posts;
	}

	@Test
	void 성공_음악_공유_게시글을_대결가능_기준으로_조회할_수_있다() {
		// given
		List<Post> testPosts = getPosts(isPossibleBattle);
		PostsFindResponseDto expected = getPostsDto(testPosts);

		when(postRepository.findByIsPossibleBattle(isPossibleBattle)).thenReturn(testPosts);

		// when
		PostsFindResponseDto posts = postService.findAllPosts(null, isPossibleBattle);

		// then
		assertThat(posts).isEqualTo(expected);

		verify(postRepository).findByIsPossibleBattle(isPossibleBattle);
	}

	private List<Post> getPosts(boolean isPossibleBattle) {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, genre, musicUrl,
				content, isPossibleBattle, member);
			posts.add(post);
		}
		return posts;
	}

	@Test
	void 성공_음악_공유_게시글을_장르와_대결가능_기준으로_조회할_수_있다() {
		// given
		List<Post> testPosts = getPosts(genre, isPossibleBattle);
		PostsFindResponseDto expected = getPostsDto(testPosts);

		when(postRepository.findByMusic_GenreAndIsPossibleBattle(genre, isPossibleBattle))
			.thenReturn(testPosts);

		// when
		PostsFindResponseDto posts = postService.findAllPosts(genre, isPossibleBattle);

		// then
		assertThat(posts).isEqualTo(expected);

		verify(postRepository).findByMusic_GenreAndIsPossibleBattle(genre, isPossibleBattle);
	}

	private List<Post> getPosts(Genre genre, boolean isPossibleBattle) {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, genre, musicUrl,
				content, isPossibleBattle, member);
			posts.add(post);
		}
		return posts;
	}

	@Test
	void 성공_음악_공유_게시글을_id로_상세조회할_수_있다() {
		// given
		Post post = getPosts().get(0);
		PostDetailFindResponseDto expected = PostDetailFindResponseDto.of(post);

		when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

		// when
		PostDetailFindResponseDto postDto = postService.findPostById(post.getId());

		// then
		assertThat(postDto).isEqualTo(expected);

		verify(postRepository).findById(post.getId());
	}

	@Test
	void 실패_존재하지_않는_음악_공유_게시글을_id이면_EntityNotFoundException_예외_발생() {
		// given
		Long wrongId = 0L;

		when(postRepository.findById(wrongId)).thenReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> postService.findPostById(wrongId))
			.isExactlyInstanceOf(EntityNotFoundException.class);

		verify(postRepository).findById(wrongId);
	}

	@Test
	void 성공_대결곡_후보_공유글_리스트를_조회할_수_있다() {
		// given
		List<Post> posts = getPosts(member, genre);
		PostsBattleCandidateResponseDto expected = getPostsBattleDto(posts);

		when(postRepository.findByMemberAndMusic_GenreAndIsPossibleBattleIsTrue(any(), any()))
			.thenReturn(posts);

		// when
		PostsBattleCandidateResponseDto postsDto = postService.findAllBattleCandidates(member, genre);

		// then
		assertThat(postsDto).isEqualTo(expected);

		verify(postRepository).findByMemberAndMusic_GenreAndIsPossibleBattleIsTrue(any(), any());
	}

	private List<Post> getPosts(Member member, Genre genre) {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, genre, musicUrl,
				content, isPossibleBattle, member);
			posts.add(post);
		}
		return posts;
	}

	private PostsFindResponseDto getPostsDto(List<Post> posts) {
		PostsFindResponseDto postsDto = PostsFindResponseDto.create();
		posts.forEach(post -> postsDto.posts().add(PostFindResponseDto.of(post)));
		return postsDto;
	}

	private PostsBattleCandidateResponseDto getPostsBattleDto(List<Post> posts) {
		PostsBattleCandidateResponseDto postsDto = PostsBattleCandidateResponseDto.create();
		posts.forEach(post -> postsDto.posts().add(PostBattleCandidateResponseDto.of(post)));
		return postsDto;
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
