package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Like;
import com.example.demo.model.post.Post;

public interface LikeRepository extends JpaRepository<Like, Long> {
	boolean existsByMemberAndPost(Member member, Post post);

	void deleteByMemberAndPost(Member member, Post post);

	@Query("select l from Like l left join fetch l.post "
		+ "where l.member.id = :memberId and l.post.music.genre = :genre order by l.id desc ")
	List<Like> findAllByMemberAndGenreLimitOrderByIdDesc(
		@Param("memberId") Long memberId, @Param("genre") Genre genre, Pageable pageable);

	List<Like> findAllByMemberIdAndPost_Music_GenreOrderByIdDesc(Long memberId, Genre genre);

	@Query("select l from Like l  where l.member.id = :memberId order by l.id desc ")
	List<Like> findAllByMemberLimitOrderByIdDesc(@Param("memberId") Long memberId, Pageable pageable);

	List<Like> findAllByMemberIdOrderByIdDesc(Long memberId);
}
