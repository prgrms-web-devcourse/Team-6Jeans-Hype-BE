package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.example.demo.dto.post.PostBattleCandidateResponseDto;
import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.dto.post.PostDetailFindResponseDto;
import com.example.demo.dto.post.PostFindResponseDto;
import com.example.demo.dto.post.PostLikeResponseDto;
import com.example.demo.dto.post.PostsBattleCandidateResponseDto;
import com.example.demo.dto.post.PostsFindResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.LikeRepository;
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
	@Mock
	private LikeRepository likeRepository;
	@Mock
	private PrincipalService principalService;
	@Mock
	private Principal principal;

	private final String musicId = "musicId";
	private final String musicName = "musicName";
	private final String musicUrl = "musicUrl";
	private final String albumCoverUrl = "albumCoverUrl";
	private final String content = "recommend";
	private final Genre genre = Genre.K_POP;
	private final String singer = "hype";
	private final boolean isPossibleBattle = true;
	private final Member member = createMember();

	@Test
	void 성공_음악_공유_게시글을_등록할_수_있다() {
		// given
		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.musicId(musicId)
			.title(musicName)
			.musicUrl(musicUrl)
			.albumCoverUrl(albumCoverUrl)
			.genre(genre)
			.singer(singer)
			.isBattlePossible(isPossibleBattle)
			.content(content)
			.build();
		Post post = postCreateRequestDto.toEntity(member);

		when(postRepository.save(any())).thenReturn(post);
		when(principalService.getMemberByPrincipal(principal)).thenReturn(member);

		// when
		Long postId = postService.createPost(principal, postCreateRequestDto);

		// then
		assertThat(postId).isEqualTo(post.getId());

		verify(postRepository).save(any());
		verify(principalService).getMemberByPrincipal(principal);
	}

	@Test
	void 성공_등록할_게시글의_대결가능여부를_확인할_수_있다() {
		// given
		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.musicId(musicId)
			.title(musicName)
			.musicUrl(musicUrl)
			.albumCoverUrl(albumCoverUrl)
			.genre(genre)
			.singer(singer)
			.isBattlePossible(true)
			.content(content)
			.build();

		Post post = postCreateRequestDto.toEntity(member);

		// when
		when(postRepository.save(any())).thenReturn(post);
		when(postRepository.existsByMemberAndMusic_MusicId(any(), any())).thenReturn(false);
		when(principalService.getMemberByPrincipal(principal)).thenReturn(member);

		postService.createPost(principal, postCreateRequestDto);

		// then
		assertThat(post.isPossibleBattle()).isEqualTo(true);

		verify(postRepository).save(any());
		verify(postRepository).existsByMemberAndMusic_MusicId(any(), any());
		verify(principalService).getMemberByPrincipal(principal);
	}

	@Test
	void 성공_대결가능한_게시글을_등록한_유저는_도전권을_얻을_수_있다() {
		// given
		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.musicId(musicId)
			.title(musicName)
			.musicUrl(musicUrl)
			.albumCoverUrl(albumCoverUrl)
			.genre(genre)
			.singer(singer)
			.isBattlePossible(true)
			.content(content)
			.build();

		Post post = postCreateRequestDto.toEntity(member);

		int originChallengeTicketCount = member.getCountOfChallengeTicket();

		// when
		when(postRepository.save(any())).thenReturn(post);
		when(postRepository.existsByMemberAndMusic_MusicId(any(), any())).thenReturn(false);
		when(principalService.getMemberByPrincipal(principal)).thenReturn(member);

		postService.createPost(principal, postCreateRequestDto);

		// then
		assertThat(member.getCountOfChallengeTicket()).isEqualTo(originChallengeTicketCount + 1);

		verify(postRepository).save(any());
		verify(postRepository).existsByMemberAndMusic_MusicId(any(), any());
		verify(principalService).getMemberByPrincipal(principal);
	}

	@Test
	void 실패_한_유저는_중복된_music_id는_등록할_수_없다() {
		// given
		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.musicId(musicId)
			.title(musicName)
			.musicUrl(musicUrl)
			.albumCoverUrl(albumCoverUrl)
			.genre(genre)
			.singer(singer)
			.isBattlePossible(isPossibleBattle)
			.content(content)
			.build();

		when(postRepository.existsByMemberAndMusic_MusicId(any(), any())).thenReturn(true);

		// when then
		assertThatThrownBy(() -> postService.createPost(principal, postCreateRequestDto))
			.isExactlyInstanceOf(IllegalArgumentException.class);

		verify(postRepository).existsByMemberAndMusic_MusicId(any(), any());
	}

	@Test
	void 성공_음악_공유_게시글을_모두_조회할_수_있다() {
		// given
		List<Post> testPosts = getPosts();
		PostsFindResponseDto expected = getPostsDto(testPosts);

		when(postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))).thenReturn(testPosts);

		// when
		PostsFindResponseDto posts = postService.findAllPosts(null, null);

		// then
		assertThat(posts).isEqualTo(expected);

		verify(postRepository).findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
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

		when(postRepository.findByMusic_Genre(genre, Sort.by(Sort.Direction.DESC, "createdAt"))).thenReturn(testPosts);

		// when
		PostsFindResponseDto posts = postService.findAllPosts(genre, null);

		// then
		assertThat(posts).isEqualTo(expected);

		verify(postRepository).findByMusic_Genre(genre, Sort.by(Sort.Direction.DESC, "createdAt"));
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

		when(postRepository.findByIsPossibleBattle(isPossibleBattle,
			Sort.by(Sort.Direction.DESC, "createdAt"))).thenReturn(testPosts);

		// when
		PostsFindResponseDto posts = postService.findAllPosts(null, isPossibleBattle);

		// then
		assertThat(posts).isEqualTo(expected);

		verify(postRepository).findByIsPossibleBattle(isPossibleBattle, Sort.by(Sort.Direction.DESC, "createdAt"));
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

		when(postRepository.findByMusic_GenreAndIsPossibleBattle(genre, isPossibleBattle,
			Sort.by(Sort.Direction.DESC, "createdAt")))
			.thenReturn(testPosts);

		// when
		PostsFindResponseDto posts = postService.findAllPosts(genre, isPossibleBattle);

		// then
		assertThat(posts).isEqualTo(expected);

		verify(postRepository).findByMusic_GenreAndIsPossibleBattle(genre, isPossibleBattle,
			Sort.by(Sort.Direction.DESC, "createdAt"));
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
		PostsBattleCandidateResponseDto postsDto = postService.findAllBattleCandidates(principal, genre);

		// then
		assertThat(postsDto).isEqualTo(expected);

		verify(postRepository).findByMemberAndMusic_GenreAndIsPossibleBattleIsTrue(any(), any());
	}

	@Test
	void 성공_추천글_좋아요_등록() {
		// given
		Post post = getPosts().get(0);

		// when
		when(postRepository.findById(0L)).thenReturn(Optional.of(post));
		when(likeRepository.existsByMemberAndPost(member, post)).thenReturn(false);

		PostLikeResponseDto result = postService.likePost(member, 0L);

		// then
		assertThat(result.hasLike()).isEqualTo(true);
		assertThat(post.getLikeCount()).isEqualTo(1);

		verify(postRepository).findById(0L);
		verify(likeRepository).existsByMemberAndPost(member, post);
	}

	@Test
	void 성공_추천글_좋아요_해제() {
		// given
		Post post = getPosts().get(0);
		post.plusLike();

		// when
		when(postRepository.findById(0L)).thenReturn(Optional.of(post));
		when(likeRepository.existsByMemberAndPost(member, post)).thenReturn(true);

		PostLikeResponseDto result = postService.likePost(member, 0L);

		// then
		assertThat(result.hasLike()).isEqualTo(false);
		assertThat(post.getLikeCount()).isEqualTo(0);

		verify(postRepository).findById(0L);
		verify(likeRepository).existsByMemberAndPost(member, post);
	}

	@Test
	void 성공_전체_좋아요_상위_10개_추천글_조회() {
		// given
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, genre, musicUrl,
				content, isPossibleBattle, member);
			for (int j = 0; j < 10 - i; j++) {
				post.plusLike();
			}
			posts.add(post);
		}

		PostsFindResponseDto postsFindResponseDto = PostsFindResponseDto.of(
			posts.stream().map(PostFindResponseDto::of).toList()
		);

		Sort sort = Sort.by(Sort.Direction.DESC, "likeCount");

		// when
		when(postRepository.findAll(sort)).thenReturn(posts);

		PostsFindResponseDto result = postService.findTenPostsByLikeCount(null);

		// then
		assertThat(result).isEqualTo(postsFindResponseDto);

		verify(postRepository).findAll(sort);
	}

	@Test
	void 성공_장르별_좋아요_상위_10개_추천글_조회() {
		// given
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 11; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, genre, musicUrl,
				content, isPossibleBattle, member);
			for (int j = 0; j < 11 - i; j++) {
				post.plusLike();
			}
			posts.add(post);
		}

		Sort sort = Sort.by(Sort.Direction.DESC, "likeCount");

		// when
		when(postRepository.findByMusic_Genre(genre, sort)).thenReturn(posts);

		PostsFindResponseDto result = postService.findTenPostsByLikeCount(genre);

		posts.remove(posts.size() - 1);
		PostsFindResponseDto postsFindResponseDto = PostsFindResponseDto.of(
			posts.stream().map(PostFindResponseDto::of).toList()
		);

		// then
		assertThat(result.posts().size()).isEqualTo(10);
		assertThat(result).isEqualTo(postsFindResponseDto);

		verify(postRepository).findByMusic_Genre(genre, sort);
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
		return PostsFindResponseDto.of(posts.stream()
			.map(PostFindResponseDto::of)
			.toList());
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
			.refreshToken("token")
			.socialType(Social.GOOGLE)
			.socialId("socialId")
			.build();
	}
}
