package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	@Query("select m from Member m left join fetch m.posts where m.id = :memberId")
	Optional<Member> findByIdFetchMember(Long memberId);
}
