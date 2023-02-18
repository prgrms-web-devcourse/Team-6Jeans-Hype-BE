package com.example.demo.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(HealthController.class)
class HealthControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void 성공_헬스체크_요청을했을때_특정문자열반환() throws Exception {
		//given
		String name = "test";
		//when
		ResultActions healthRequest = mockMvc.perform(get("/hello?name={name}", name)
			.accept(MediaType.TEXT_PLAIN));
		//then
		healthRequest.andExpect(status().is2xxSuccessful())
			.andDo(
				document("heath-controller",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestParameters(
						parameterWithName("name").description("이름을 담습니다. default : test")
					),
					responseBody()
				)
			).andDo(print());
	}
}
