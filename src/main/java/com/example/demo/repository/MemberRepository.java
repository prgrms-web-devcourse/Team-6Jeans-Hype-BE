package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findBySocialInfo_SocialTypeAndSocialInfo_SocialId(Social socialType, String socialId);

}
