package com.example.demo.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.example.demo.dto.post.PostLikeResponseDto;
import com.example.demo.model.member.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLockFacade {

	private final PostService postService;
	private final RedissonClient redissonClient;

	public PostLikeResponseDto likePost(Member member, Long postId) {
		RLock lock = redissonClient.getLock(String.format("like:post:%d", postId));

		try {
			boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
			if (!available) {
				throw new RuntimeException("Lock을 획득하지 못했습니다.");
			}
			return postService.likePost(member, postId);
		} catch (InterruptedException error) {
			throw new RuntimeException(error);
		} finally {
			lock.unlock();
		}
	}

}
