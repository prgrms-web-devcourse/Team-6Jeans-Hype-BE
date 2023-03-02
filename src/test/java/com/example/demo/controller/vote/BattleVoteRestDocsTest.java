package com.example.demo.controller.vote;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.demo.controller.BattleController;
import com.example.demo.dto.vote.BattleVoteRequestDto;
import com.example.demo.dto.vote.VoteResultResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.security.TokenAuthenticationFilter;
import com.example.demo.service.BattleService;
import com.example.demo.service.PrincipalService;
import com.example.demo.service.VoteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WithMockUser
@ExtendWith({MockitoExtension.class})
@AutoConfigureRestDocs
@WebMvcTest(
	value = BattleController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = TokenAuthenticationFilter.class
	)
)
class BattleVoteRestDocsTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private ObjectMapper mapper = new ObjectMapper();

	@MockBean
	private PrincipalService principalService;

	@MockBean
	private BattleService battleService;

	@MockBean
	private VoteService voteService;

	@Test
	void 성공_진행중인_대결중_하나의_음악에_투표할_수_있다() throws Exception {
		// given
		Member member = createMember();
		BattleVoteRequestDto request = createRequest();
		VoteResultResponseDto response = createResponse();
		// when
		when(principalService.getMemberByPrincipal(any(Principal.class)))
			.thenReturn(member);
		when(voteService.voteBattle(any(), anyLong(), anyLong()))
			.thenReturn(response);
		ResultActions actions = mockMvc.perform(
			post("/api/v1/battles/vote")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request))
				.with(csrf()));
		// then
		verify(principalService).getMemberByPrincipal(any());
		verify(voteService).voteBattle(any(), anyLong(), anyLong());
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("success").value(true))
			.andExpect(jsonPath("data").exists())
			.andDo(document("success-battle-vote",
				requestFields(
					fieldWithPath("battleId").type(JsonFieldType.NUMBER).description("대결 게시글 ID"),
					fieldWithPath("votedPostId").type(JsonFieldType.NUMBER).description("투표된 추천 게시글 ID")
				),
				responseFields(
					fieldWithPath("success").type(JsonFieldType.BOOLEAN)
						.description("API 요청 성공 여부"),
					fieldWithPath("message").type(JsonFieldType.STRING)
						.description("API 요청 응답 메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT)
						.description("API 요청 응답 데이터"),
					fieldWithPath("data.title").type(JsonFieldType.STRING)
						.description("투표된 음악 제목"),
					fieldWithPath("data.albumCoverUrl").type(JsonFieldType.STRING)
						.description("투표된 음악 앨범 커버 URL"),
					fieldWithPath("data.selectedPostVoteCnt").type(JsonFieldType.NUMBER)
						.description("투표된 음악 추천 게시글 득표수"),
					fieldWithPath("data.oppositePostVoteCnt").type(JsonFieldType.NUMBER)
						.description("투표되지 않은 음악 추천 게시글 득표 수")
				))
			);
	}

	private Member createMember() {
		return new Member("https://hype.music/images/1",
			"nickname",
			"refreshToken",
			Social.GOOGLE,
			"socialId");
	}

	private BattleVoteRequestDto createRequest() {
		return new BattleVoteRequestDto(1L, 1L);
	}

	private VoteResultResponseDto createResponse() {
		return new VoteResultResponseDto(
			"title",
			"albumCoverUrl",
			4,
			10
		);
	}
}
