package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByMusic_GenreAndIsPossibleBattle(Genre genre, boolean isPossibleBattle);

	List<Post> findByMusic_Genre(Genre genre);

	List<Post> findByIsPossibleBattle(boolean isPossibleBattle);
}
