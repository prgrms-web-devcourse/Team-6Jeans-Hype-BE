package com.example.demo.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.example.demo.repository.MemberRepository;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	MemberRepository memberRepository;

	@BeforeAll
	void setup() {
		memberRepository.save(TestUtil.createMember("가나다라마바사"));
	}

	@Test
	@WithMockUser(username = "1")
	void 성공_잘못되지않은_accessToken_200() throws Exception {
		ResultActions resultActions = mockMvc.perform(get("/api/v1/auth/login-check")
			.header("Authorization", "Bearer {AccessToken}"));

		resultActions.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("logined-user", resource(
				ResourceSnippetParameters.builder().tag("auth")
					.description("사용자가 로그인 했는지 여부를 체크합니다.")
					.requestHeaders(
						headerWithName("Authorization").description("Hype 서비스 Access Token")
					).responseFields(
						fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
						fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
						fieldWithPath("data").type(JsonFieldType.OBJECT).description("API 요청 응답 메시지"),
						fieldWithPath("data.isLogin").type(JsonFieldType.BOOLEAN).description("로그인 됐는지 여부")
					)
					.build()
			)));
	}

	@Test
	@WithAnonymousUser
	void 실패_잘못된_accessToken_401() throws Exception {
		ResultActions resultActions = mockMvc.perform(get("/api/v1/auth/login-check")
			.header("Authorization", "Bearer {AccessToken}"));

		resultActions
			.andExpect(status().isUnauthorized())
			.andDo(print())
			.andDo(document("wrong-accessToken-user", resource(
				ResourceSnippetParameters.builder().tag("auth")
					.description("사용자가 로그인 했는지 여부를 체크합니다.")
					.requestHeaders(
						headerWithName("Authorization").description("Hype 서비스 Access Token")
					).responseFields(
						fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
						fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
						fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - null")
					)
					.build()
			)));
	}
}
