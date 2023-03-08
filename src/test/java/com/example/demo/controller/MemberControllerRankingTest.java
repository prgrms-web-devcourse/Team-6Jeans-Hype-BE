package com.example.demo.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.example.demo.controller.TestUtil.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberControllerRankingTest {
	private static final String RANKING_API_NAME = "ranking";
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	MockMvc mockMvc;
	List<Member> dummyMembers = new ArrayList<>();
	int countOfDummyMember = 8;

	@BeforeAll
	void setUpDummyMembers() {
		for (int i = 0; i < countOfDummyMember; i++) {
			Member dummyMember = createMember("member%d".formatted(i));
			dummyMember.updateMemberScore(countOfDummyMember - i, 100 + i, 187 - i);
			dummyMembers.add(dummyMember);
		}
		memberRepository.saveAll(dummyMembers);
	}

	@Test
	void 성공_멤버랭킹조회_Top100을_조회할_수_있다_200() throws Exception {
		//given
		String targetEndPoint = "/api/v1/members/ranking";
		//when
		ResultActions resultActions = mockMvc.perform(get(targetEndPoint));

		//then
		resultActions.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("top-100-find",
				resource(
					ResourceSnippetParameters.builder().tag(RANKING_API_NAME)
						.description("유저 랭킹 top100 조회 랭킹순으로 정렬")
						.responseFields(
							fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
							fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
							fieldWithPath("data").type(OBJECT).description("API 응답 데이터"),
							fieldWithPath("data.duration").type(JsonFieldType.OBJECT)
								.description("랭킹 산정 기간에 대한 정보"),
							fieldWithPath("data.duration.from").type(JsonFieldType.STRING)
								.description("랭킹 산정 시작 일"),
							fieldWithPath("data.duration.to").type(JsonFieldType.STRING)
								.description("랭킹 산정 종료 일"),
							fieldWithPath("data.ranking").type(JsonFieldType.ARRAY)
								.description("유저 랭킹 배열"),
							fieldWithPath("data.ranking[].memberId").type(JsonFieldType.NUMBER)
								.description("유저 id"),
							fieldWithPath("data.ranking[].memberNickname").type(JsonFieldType.STRING)
								.description("유저 닉네임"),
							fieldWithPath("data.ranking[].memberRanking").type(JsonFieldType.NUMBER)
								.description("유저 랭킹"),
							fieldWithPath("data.ranking[].memberPoint").type(JsonFieldType.NUMBER)
								.description("유저 포인트")
						)
						.build()
				)
			)).andExpect(jsonPath("$.data.ranking.length()").value(countOfDummyMember));
	}

	@Test
	void 성공_멤버랭킹조회_상위5명을_조회할_수_있다_200() throws Exception {
		//given
		String targetEndPoint = "/api/v1/members/ranking/preview";
		//when
		ResultActions resultActions = mockMvc.perform(get(targetEndPoint));

		//then
		resultActions.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("top5-find",
				resource(
					ResourceSnippetParameters.builder().tag(RANKING_API_NAME)
						.description("유저 랭킹 top5 조회 랭킹순으로 정렬")
						.responseFields(
							fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
							fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
							fieldWithPath("data").type(OBJECT).description("API 응답 데이터"),
							fieldWithPath("data.duration").type(JsonFieldType.OBJECT)
								.description("랭킹 산정 기간에 대한 정보"),
							fieldWithPath("data.duration.from").type(JsonFieldType.STRING)
								.description("랭킹 산정 시작 일"),
							fieldWithPath("data.duration.to").type(JsonFieldType.STRING)
								.description("랭킹 산정 종료 일"),
							fieldWithPath("data.ranking").type(JsonFieldType.ARRAY)
								.description("유저 랭킹 배열"),
							fieldWithPath("data.ranking[].memberId").type(JsonFieldType.NUMBER)
								.description("유저 id"),
							fieldWithPath("data.ranking[].memberNickname").type(JsonFieldType.STRING)
								.description("유저 닉네임"),
							fieldWithPath("data.ranking[].memberRanking").type(JsonFieldType.NUMBER)
								.description("유저 랭킹"),
							fieldWithPath("data.ranking[].memberPoint").type(JsonFieldType.NUMBER)
								.description("유저 포인트")
						)
						.build()
				)
			)).andExpect(jsonPath("$.data.ranking.length()").value(5));
	}

	@Test
	void 성공_멤버랭킹조회_중복자가있을경우_중복자포함_상위5명을_조회할_수_있다_200() throws Exception {
		//given
		String targetEndPoint = "/api/v1/members/ranking/preview";
		Member firstRanker = dummyMembers.get(countOfDummyMember - 1);
		Member newMember1 = createMember("newMember1");
		Member newMember2 = createMember("newMember2");
		newMember1.updateMemberScore(firstRanker.getRanking(), firstRanker.getVictoryPoint(),
			firstRanker.getVictoryCount());
		newMember2.updateMemberScore(firstRanker.getRanking(), firstRanker.getVictoryPoint(),
			firstRanker.getVictoryCount());
		memberRepository.save(newMember1);
		memberRepository.save(newMember2);
		//when
		ResultActions resultActions = mockMvc.perform(get(targetEndPoint));

		//then
		resultActions.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("top5-find-duplicate-ranking",
				resource(
					ResourceSnippetParameters.builder().tag(RANKING_API_NAME)
						.description("유저 랭킹 top100 조회 랭킹순으로 정렬")
						.responseFields(
							fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
							fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
							fieldWithPath("data").type(OBJECT).description("API 응답 데이터"),
							fieldWithPath("data.duration").type(JsonFieldType.OBJECT)
								.description("랭킹 산정 기간에 대한 정보"),
							fieldWithPath("data.duration.from").type(JsonFieldType.STRING)
								.description("랭킹 산정 시작 일"),
							fieldWithPath("data.duration.to").type(JsonFieldType.STRING)
								.description("랭킹 산정 종료 일"),
							fieldWithPath("data.ranking").type(JsonFieldType.ARRAY)
								.description("유저 랭킹 배열"),
							fieldWithPath("data.ranking[].memberId").type(JsonFieldType.NUMBER)
								.description("유저 id"),
							fieldWithPath("data.ranking[].memberNickname").type(JsonFieldType.STRING)
								.description("유저 닉네임"),
							fieldWithPath("data.ranking[].memberRanking").type(JsonFieldType.NUMBER)
								.description("유저 랭킹"),
							fieldWithPath("data.ranking[].memberPoint").type(JsonFieldType.NUMBER)
								.description("유저 포인트")
						)
						.build()
				)
			)).andExpect(jsonPath("$.data.ranking.length()").value(5));
	}

}
