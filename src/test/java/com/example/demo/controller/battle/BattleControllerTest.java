package com.example.demo.controller.battle;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

@WithMockUser
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(value = BattleController.class)
class BattleControllerTest {
	@Autowired
	private MockMvc mockMvc;
	private ObjectMapper mapper = new ObjectMapper();

	@Test
	public void 성공_배틀신청_배틀신청_요청을_보내면_배틀을_생성한다_201_반환() throws Exception {
		//given
		BattleCreateRequestDto battleCreateRequestDto = BattleCreateRequestDto
			.builder()
			.challengedPostId(1)
			.challengedPostId(2)
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/battles")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(battleCreateRequestDto))
			.with(csrf())
		);
		//then
		resultActions.andExpect(status().isCreated())
			.andExpect(header().string("Location", "http://localhost:8080/api/v1/battles/0"))
			.andDo(print())
			.andDo(document(
				"battle-post",
				resource(
					ResourceSnippetParameters.builder()
						.description("배틀 신청 API입니다.")
						.requestFields(
							fieldWithPath("challengedPostId").type(NUMBER).description("도전받는 post의 id"),
							fieldWithPath("challengingPostId").type(NUMBER).description("도전하는 post의 id")
						)
						.requestHeaders(
							headerWithName("Authorization").description("Hype 서비스 Access Token")
						).responseHeaders(
							headerWithName("Location").description("접근가능한 url")
						).responseFields(
							fieldWithPath("success").type(BOOLEAN).description("API 요청 성공 여부"),
							fieldWithPath("message").type(STRING).description("API 요청 응답 메시지"),
							fieldWithPath("data").type(NULL).description("API 요청 응답 메시지 - Null")
						)
						.build()
				)
			));
	}

}
