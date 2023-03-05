package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.member.Member;
import com.example.demo.model.post.Like;
import com.example.demo.model.post.Post;

public interface LikeRepository extends JpaRepository<Like, Long> {
	boolean existsByMemberAndPost(Member member, Post post);

	void deleteByMemberAndPost(Member member, Post post);
}
