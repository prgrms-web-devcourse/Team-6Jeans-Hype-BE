package com.example.demo.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.model.post.Genre;
import com.example.demo.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PostController.class)
@WithMockUser
@ExtendWith(MockitoExtension.class)
@AutoConfigureRestDocs
class PostControllerTest {

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private MockMvc mockMvc;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private ObjectMapper mapper = new ObjectMapper();

	@MockBean
	private PostService postService;

	private final String musicId = "musicId";
	private final String musicName = "musicName";
	private final String musicUrl = "musicUrl";
	private final String albumCoverUrl = "albumCoverUrl";
	private final Genre genre = Genre.DANCE;
	private final String singer = "hype";
	private final boolean isPossibleBattle = true;
	private final String content = "comment";

	@Test
	@Async("webExecutor")
	void 성공_음악_공유_게시글을_등록할_수_있다() throws Exception {
		// given
		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.musicId(musicId)
			.musicName(musicName)
			.musicUrl(musicUrl)
			.albumCoverUrl(albumCoverUrl)
			.genre(genre)
			.singer(singer)
			.isBattlePossible(isPossibleBattle)
			.content(content)
			.build();

		// when
		ResultActions resultActions = mockMvc.perform(
			post("/api/v1/posts")
				.contentType(APPLICATION_JSON)
				.content(mapper.writeValueAsString(postCreateRequestDto))
				.with(csrf())
		);

		// then
		resultActions.andExpect(status().isCreated())
			.andExpect(header().string("Location", "http://localhost:8080/api/v1/posts/0"))
			.andDo(print())
			.andDo(document("Save Post",
				requestFields(
					fieldWithPath("musicId").type(STRING).description("등록할 음악의 id 값"),
					fieldWithPath("musicName").type(STRING).description("등록할 음악의 제목"),
					fieldWithPath("musicUrl").type(STRING).description("등록할 음악의 url"),
					fieldWithPath("albumCoverUrl").type(STRING).description("등록할 음악의 앨범 표지 이미지 url"),
					fieldWithPath("genre").type(STRING).description("등록할 음악의 장르 값"),
					fieldWithPath("singer").type(STRING).description("등록할 음악의 가수명"),
					fieldWithPath("isBattlePossible").type(BOOLEAN).description("등록할 게시글의 배틀 가능 여부"),
					fieldWithPath("content").type(STRING).description("등록할 게시글의 내용")
				),
				responseHeaders(
					headerWithName("Location").description("등록된 게시글 조회를 위한 URI")
				),
				responseFields(
					fieldWithPath("success").type(BOOLEAN).description("API 요청 성공 여부"),
					fieldWithPath("message").type(STRING).description("API 요청 응답 메시지"),
					fieldWithPath("data").type(NULL).description("API 요청 응답 메시지 (null)")
				)
			));
	}

}
