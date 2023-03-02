package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByMusic_GenreAndIsPossibleBattle(Genre genre, boolean isPossibleBattle);

	List<Post> findByMusic_Genre(Genre genre);

	List<Post> findByIsPossibleBattle(boolean isPossibleBattle);

	List<Post> findByMemberAndMusic_GenreAndIsPossibleBattleIsTrue(Member member, Genre genre);

	boolean existsByMemberAndMusic_MusicId(Member member, String musicId);

	List<Post> findByMemberAndIsPossibleBattleIsTrue(Member member);
}
