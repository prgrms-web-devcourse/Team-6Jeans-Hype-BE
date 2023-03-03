package com.example.demo.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;

	private final Genre genre = Genre.K_POP;
	private final boolean isPossibleBattle = true;
	private final Member member = createMember();

	@Test
	void 음악_공유_게시글을_장르와_대결가능여부_기준으로_조회할_수_있다() {
		// given
		List<Post> posts = getPosts(genre, isPossibleBattle);
		memberRepository.save(member);
		postRepository.saveAll(posts);

		Collections.reverse(posts);

		// when
		List<Post> actual = postRepository
			.findByMusic_GenreAndIsPossibleBattle(genre, isPossibleBattle, Sort.by(Sort.Direction.DESC, "createdAt"));

		// then
		assertThat(actual).isEqualTo(posts);
	}

	private List<Post> getPosts(Genre genre, boolean isPossibleBattle) {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = Post.create("mid", "album", "singer", "title",
				genre, "url", "content", isPossibleBattle, member);
			posts.add(post);
		}
		return posts;
	}

	@Test
	void 음악_공유_게시글을_장르_기준으로_조회할_수_있다() {
		// given
		List<Post> posts = getPosts(genre);
		memberRepository.save(member);
		postRepository.saveAll(posts);

		// when
		List<Post> actual = postRepository.findByMusic_Genre(genre, Sort.by(Sort.Direction.DESC, "createdAt"));

		// then
		assertThat(actual).isEqualTo(posts);
	}

	private List<Post> getPosts(Genre genre) {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = Post.create("mid", "album", "singer", "title",
				genre, "url", "content", isPossibleBattle, member);
			posts.add(post);
		}
		return posts;
	}

	@Test
	void 음악_공유_게시글을_대결가능여부_기준으로_조회할_수_있다() {
		// given
		List<Post> posts = getPosts(isPossibleBattle);
		memberRepository.save(member);
		postRepository.saveAll(posts);

		// when
		List<Post> actual = postRepository.findByIsPossibleBattle(isPossibleBattle,
			Sort.by(Sort.Direction.DESC, "createdAt"));

		// then
		assertThat(actual).isEqualTo(posts);
	}

	private List<Post> getPosts(boolean isPossibleBattle) {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = Post.create("mid", "album", "singer", "title",
				genre, "url", "content", isPossibleBattle, member);
			posts.add(post);
		}
		return posts;
	}

	@Test
	void 음악_대결곡_후보_리스트를_조회할_수_있다() {
		// given
		List<Post> posts = getPosts(member, genre);
		memberRepository.save(member);
		postRepository.saveAll(posts);

		// when
		List<Post> actual = postRepository.findByMemberAndMusic_GenreAndIsPossibleBattleIsTrue(member, genre);

		// then
		assertThat(actual).isEqualTo(posts);
	}

	private List<Post> getPosts(Member member, Genre genre) {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = Post.create("mid", "album", "singer", "title",
				genre, "url", "content", isPossibleBattle, member);
			posts.add(post);
		}
		return posts;
	}

	@Test
	void 같은_유저와_음악_id가_존재함을_알_수_있다() {
		// given
		String musicId = "musicId";
		Post post = Post.create(musicId, "album", "singer", "title", genre,
			"url", "content", isPossibleBattle, member);

		memberRepository.save(member);
		postRepository.save(post);

		// when
		boolean isExisted = postRepository.existsByMemberAndMusic_MusicId(member, musicId);

		// then
		assertThat(isExisted).isEqualTo(true);
	}

	@Test
	void 특정_유저의_대결가능한_대결_리스트를_조회할_수_있다() {
		// given
		memberRepository.save(member);
		List<Post> posts = getPosts();

		posts = posts.stream().filter(Post::isPossibleBattle).toList();

		// when
		List<Post> result = postRepository.findByMemberAndIsPossibleBattleIsTrue(member);

		// then
		assertThat(result).isEqualTo(posts);
	}

	private List<Post> getPosts() {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = createPost(member);
			posts.add(post);
			member.getPosts().add(post);
			postRepository.save(post);
		}
		return posts;
	}

	private Post createPost(Member member) {
		return Post.create(
			"musicId",
			"albumCoverUrl",
			"hype",
			"musicName",
			Genre.K_POP,
			"musicUrl",
			"recommend",
			true,
			member
		);
	}

	@Test
	@Transactional
	void 성공_게시글ID와_Battle가능여부로_Post를_가져올_수_있다() {
		String musicId = "123456789";
		Post post = Post.create(musicId, "album", "singer", "title", genre,
			"url", "content", isPossibleBattle, member);
		memberRepository.save(member);
		postRepository.save(post);
		List<Post> posts = postRepository.findAll();
		for (Post exsistPost : posts) {
			Long exsistPostId = exsistPost.getId();
			Optional<Post> postByIdAndIsPossibleBattle = postRepository.findPostByIdAndIsPossibleBattle(exsistPostId,
				true);
			assertThat(postByIdAndIsPossibleBattle.get())
				.usingRecursiveComparison().ignoringFields("id")
				.isEqualTo(post);
			Optional<Post> optionalPost = postRepository.findPostByIdAndIsPossibleBattle(exsistPostId,
				false);
			assertThat(optionalPost.isEmpty()).isEqualTo(true);

		}

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
