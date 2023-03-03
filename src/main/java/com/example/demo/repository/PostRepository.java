package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByMusic_GenreAndIsPossibleBattle(Genre genre, boolean isPossibleBattle);

	List<Post> findByMusic_Genre(Genre genre);

	List<Post> findByIsPossibleBattle(boolean isPossibleBattle);

	Optional<Post> findPostByIdAndIsPossibleBattle(Long id, boolean isPossibleBattle);

	List<Post> findByMemberAndMusic_GenreAndIsPossibleBattleIsTrue(Member member, Genre genre);

	boolean existsByMemberAndMusic_MusicId(Member member, String musicId);

	List<Post> findAllByMemberIdOrderByIdDesc(Long memberId);

	List<Post> findAllByMemberIdAndMusic_GenreOrderByIdDesc(Long memberId, Genre genre);

	@Query("select p from Post p left join fetch p.member where p.member.id = :memberId order by p.id desc")
	List<Post> findAllByIdLimitOrderByIdDesc(@Param("memberId") Long memberId, Pageable pageable);

	@Query("select p from Post p "
		+ "left join fetch p.member where p.member.id = :memberId and p.music.genre = :genre "
		+ "order by p.id desc")
	List<Post> findAllByIdLimitAndGenreOrderByIdDesc(@Param("memberId") Long memberId, @Param("genre") Genre genre,
		Pageable pageable);

	List<Post> findByMemberAndIsPossibleBattleIsTrue(Member member);
}
