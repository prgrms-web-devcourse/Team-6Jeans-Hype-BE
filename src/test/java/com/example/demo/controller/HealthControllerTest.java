package com.example.demo.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.example.demo.security.TokenAuthenticationFilter;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(
	value = HealthController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = TokenAuthenticationFilter.class
	)
)

@AutoConfigureRestDocs
@AutoConfigureMockMvc
// @EnableConfigurationProperties(value = AppProperties.class)
@WithMockUser
class HealthControllerTest {
	@Autowired
	private MockMvc mockMvc;
	// @SpyBean
	// TokenProvider tokenProvider;

	@Test
	public void 성공_헬스체크_요청을했을때_특정문자열반환() throws Exception {
		//given
		String name = "test";
		//when
		ResultActions healthRequest = mockMvc.perform(get("/health?name={name}", name)
			.accept(MediaType.TEXT_PLAIN));
		//then
		healthRequest.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(name + " health"))
			.andDo(
				document("heath-get",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					resource(
						ResourceSnippetParameters.builder()
							.description("헬스체크 api 입니다.")
							.requestParameters(
								parameterWithName("name").description("이름을 담습니다. default : test")
							)
							.build()
					)
				)
			).andDo(print());
	}
}

