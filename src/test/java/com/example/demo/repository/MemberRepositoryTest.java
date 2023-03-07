package com.example.demo.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;

@DataJpaTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberRepositoryTest {
	@Autowired
	MemberRepository memberRepository;
	int numberOfDummyUser = 10;
	List<Member> members;

	@BeforeAll
	@Transactional
	void setupDummyMember() {
		members = makeDummyMemberList();
		memberRepository.saveAll(members);
		List<Member> savedMembers = memberRepository.findAll();
		for (int i = 0; i < numberOfDummyUser; i++) {
			assertThat(members.get(i)).usingRecursiveComparison()
				.ignoringFields("createdAt")
				.ignoringFields("likes")
				.ignoringFields("posts")
				.ignoringFields("updatedAt")
				.isEqualTo(savedMembers.get(i));
		}

	}

	@Test
	void 성공_랭킹을조회한다_랭킹이_0보다_클때_오름차순으로_정렬된다() {
		//given
		List<Member> membersSortedByRank = members.stream().sorted((a, b) -> a.getRanking() - b.getRanking()).toList();
		//when
		List<Member> membersSortedByRankFromRepository =
			memberRepository.findByMemberScore_RankingBetweenOrderByMemberScore_RankingAsc(1, 100);
		//then
		for (int i = 0; i < numberOfDummyUser; i++) {
			assertThat(membersSortedByRankFromRepository.get(i)).usingRecursiveComparison()
				.ignoringFields("createdAt")
				.ignoringFields("likes")
				.ignoringFields("posts")
				.ignoringFields("updatedAt")
				.isEqualTo(membersSortedByRank.get(i))
				.isNotEqualTo(members.get(i));
			;
		}
	}

	List<Member> makeDummyMemberList() {
		List<Member> dummyMembers = new ArrayList<>();
		for (int i = 0; i < numberOfDummyUser; i++) {
			Member dummyMember = Member.builder()
				.nickname("user%d".formatted(i))
				.socialId("socialId%d".formatted(i))
				.socialType(Social.GOOGLE)
				.profileImageUrl("http://example.com")
				.refreshToken("test")
				.build();
			dummyMember.updateRanking(100 - i);
			dummyMembers.add(dummyMember);
		}
		return dummyMembers;
	}
}
