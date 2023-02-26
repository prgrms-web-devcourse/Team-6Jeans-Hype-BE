package com.example.demo.controller.genre;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.security.TokenAuthenticationFilter;

@WithMockUser
@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(value = GenreController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = TokenAuthenticationFilter.class
	))
@AutoConfigureRestDocs
public class GenreAllRestDocsTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void 성공_모든_장르를_조회할_수_있다() throws Exception {
		// when
		var actions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/genres")
			.contentType(MediaType.APPLICATION_JSON));
		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("success").value(true))
			.andExpect(jsonPath("data").exists())
			.andDo(print())
			.andDo(document("find-all-genres",
				responseFields(
					fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("API 요청 응답 데이터"),
					fieldWithPath("data.genres").type(JsonFieldType.ARRAY).description("장르 정보"),
					fieldWithPath("data.genres[].genreValue").type(JsonFieldType.STRING).description("장르 enum 값"),
					fieldWithPath("data.genres[].genreName").type(JsonFieldType.STRING).description("장르명")
				)));
	}

}
