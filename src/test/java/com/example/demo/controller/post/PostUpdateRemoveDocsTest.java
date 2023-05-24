package com.example.demo.controller.post;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.example.demo.common.ExceptionMessage.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.example.demo.controller.PostController;
import com.example.demo.dto.post.PostUpdateRequestDto;
import com.example.demo.model.post.Genre;
import com.example.demo.security.TokenAuthenticationFilter;
import com.example.demo.service.PostService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;

@WithMockUser
@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
@Import({PostService.class})
@WebMvcTest(value = PostController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = TokenAuthenticationFilter.class
	)
)
public class PostUpdateRemoveDocsTest {

	private static final String POST_API_NAME = "Posts";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	PostService postService;

	private final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD,
		JsonAutoDetect.Visibility.ANY);

	@Test
	void 게시글_콘텐츠_정보를_수정할_수_있다() throws Exception {
		Long postId = 1L;
		PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder()
			.content("교환")
			.build();

		doNothing().when(postService).update(any(), anyLong(), any());
		ResultActions actions = mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken")
			.content(mapper.writeValueAsString(postUpdateRequestDto))
			.with(csrf())
		);

		verify(postService).update(any(), anyLong(), any());
		actions
			.andExpect(status().isNoContent())
			.andDo(document("게시물 수정 성공 - 게시글 한마디 내용 수정",
				resource(
					ResourceSnippetParameters.builder().tag(POST_API_NAME)
						.description("게시물 수정 API")
						.requestHeaders(
							headerWithName("Authorization").description("Hype 서비스 Access Token")
						)
						.pathParameters(
							parameterWithName("postId").description("조회할 공유 게시글 id")
						)
						.requestFields(
							fieldWithPath("musicId").type(NULL).description("수정할 음악 id"),
							fieldWithPath("title").type(NULL).description("수정할 음악 제목"),
							fieldWithPath("musicUrl").type(NULL).description("수정할 음악 재생 url"),
							fieldWithPath("albumCoverUrl").type(NULL).description("수정할 음악 앨범 커버 url"),
							fieldWithPath("genre").type(NULL).description("수정할 음악 장르"),
							fieldWithPath("singer").type(NULL).description("수정할 음악 가수 이름"),
							fieldWithPath("battlePossible").type(NULL).description("수정할 게시글 대결 가능 여부"),
							fieldWithPath("content").type(STRING).description("수정할 게시글 한마디 내용"),
							fieldWithPath("allDataNull").type(BOOLEAN).description("모든 데이터가 전부 NULL인지 여부"),
							fieldWithPath("musicDataNull").type(BOOLEAN)
								.description("음악 정보 데이터(id, title, musicRrl, coverUrl, genre, singer)가 모두 NULL인지 여부")
						)
						.build()
				)
			));
	}

	@Test
	void 게시글_음악_정보를_수정할_수_있다() throws Exception {
		Long postId = 1L;
		PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder()
			.musicId("1")
			.title("title")
			.musicUrl("musicUrl")
			.albumCoverUrl("albumConverUrl")
			.genre(Genre.EDM)
			.singer("singer")
			.build();

		doNothing().when(postService).update(any(), anyLong(), any());
		ResultActions actions = mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken")
			.content(mapper.writeValueAsString(postUpdateRequestDto))
			.with(csrf())
		);

		verify(postService).update(any(), anyLong(), any());
		actions
			.andExpect(status().isNoContent())
			.andDo(document("게시물 수정 성공 - 게시글 음악 정보 수정",
				resource(
					ResourceSnippetParameters.builder().tag(POST_API_NAME)
						.description("게시물 수정 API")
						.requestHeaders(
							headerWithName("Authorization").description("Hype 서비스 Access Token")
						)
						.pathParameters(
							parameterWithName("postId").description("조회할 공유 게시글 id")
						)
						.requestFields(
							fieldWithPath("musicId").type(STRING).description("수정할 음악 id"),
							fieldWithPath("title").type(STRING).description("수정할 음악 제목"),
							fieldWithPath("musicUrl").type(STRING).description("수정할 음악 재생 url"),
							fieldWithPath("albumCoverUrl").type(STRING).description("수정할 음악 앨범 커버 url"),
							fieldWithPath("genre").type(STRING).description("수정할 음악 장르"),
							fieldWithPath("singer").type(STRING).description("수정할 음악 가수 이름"),
							fieldWithPath("battlePossible").type(NULL).description("수정할 게시글 대결 가능 여부"),
							fieldWithPath("content").type(NULL).description("수정할 게시글 한마디 내용"),
							fieldWithPath("allDataNull").type(BOOLEAN).description("모든 데이터가 전부 NULL인지 여부"),
							fieldWithPath("musicDataNull").type(BOOLEAN)
								.description("음악 정보 데이터(id, title, musicRrl, coverUrl, genre, singer)가 모두 NULL인지 여부")
						)
						.build()
				)
			));
	}

	@Test
	void 게시글_대결가능유무_정보를_수정할_수_있다() throws Exception {
		Long postId = 1L;
		PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder()
			.battlePossible(true)
			.build();

		doNothing().when(postService).update(any(), anyLong(), any());
		ResultActions actions = mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken")
			.content(mapper.writeValueAsString(postUpdateRequestDto))
			.with(csrf())
		);

		verify(postService).update(any(), anyLong(), any());
		actions
			.andExpect(status().isNoContent())
			.andDo(document("게시물 수정 성공 - 게시글",
				resource(
					ResourceSnippetParameters.builder().tag(POST_API_NAME)
						.description("게시물 수정 API")
						.requestHeaders(
							headerWithName("Authorization").description("Hype 서비스 Access Token")
						)
						.pathParameters(
							parameterWithName("postId").description("조회할 공유 게시글 id")
						)
						.requestFields(
							fieldWithPath("musicId").type(NULL).description("수정할 음악 id"),
							fieldWithPath("title").type(NULL).description("수정할 음악 제목"),
							fieldWithPath("musicUrl").type(NULL).description("수정할 음악 재생 url"),
							fieldWithPath("albumCoverUrl").type(NULL).description("수정할 음악 앨범 커버 url"),
							fieldWithPath("genre").type(NULL).description("수정할 음악 장르"),
							fieldWithPath("singer").type(NULL).description("수정할 음악 가수 이름"),
							fieldWithPath("battlePossible").type(BOOLEAN).description("수정할 게시글 대결 가능 여부"),
							fieldWithPath("content").type(NULL).description("수정할 게시글 한마디 내용"),
							fieldWithPath("allDataNull").type(BOOLEAN).description("모든 데이터가 전부 NULL인지 여부"),
							fieldWithPath("musicDataNull").type(BOOLEAN)
								.description("음악 정보 데이터(id, title, musicRrl, coverUrl, genre, singer)가 모두 NULL인지 여부")
						)
						.build()
				)
			));
	}

	@Test
	void 게시글_모든_정보를_수정할_수_있다() throws Exception {
		Long postId = 1L;
		PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder()
			.musicId("1")
			.title("title")
			.musicUrl("musicUrl")
			.albumCoverUrl("albumConverUrl")
			.genre(Genre.EDM)
			.singer("singer")
			.battlePossible(true)
			.content("content")
			.build();

		doNothing().when(postService).update(any(), anyLong(), any());
		ResultActions actions = mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken")
			.content(mapper.writeValueAsString(postUpdateRequestDto))
			.with(csrf())
		);

		verify(postService).update(any(), anyLong(), any());
		actions
			.andExpect(status().isNoContent())
			.andDo(document("게시물 수정 성공 - 게시글 대결 가능 유무 수정",
				resource(
					ResourceSnippetParameters.builder().tag(POST_API_NAME)
						.description("게시물 수정 API")
						.requestHeaders(
							headerWithName("Authorization").description("Hype 서비스 Access Token")
						)
						.pathParameters(
							parameterWithName("postId").description("조회할 공유 게시글 id")
						)
						.requestFields(
							fieldWithPath("musicId").type(STRING).description("수정할 음악 id"),
							fieldWithPath("title").type(STRING).description("수정할 음악 제목"),
							fieldWithPath("musicUrl").type(STRING).description("수정할 음악 재생 url"),
							fieldWithPath("albumCoverUrl").type(STRING).description("수정할 음악 앨범 커버 url"),
							fieldWithPath("genre").type(STRING).description("수정할 음악 장르"),
							fieldWithPath("singer").type(STRING).description("수정할 음악 가수 이름"),
							fieldWithPath("battlePossible").type(BOOLEAN).description("수정할 게시글 대결 가능 여부"),
							fieldWithPath("content").type(STRING).description("수정할 게시글 한마디 내용"),
							fieldWithPath("allDataNull").type(BOOLEAN).description("모든 데이터가 전부 NULL인지 여부"),
							fieldWithPath("musicDataNull").type(BOOLEAN)
								.description("음악 정보 데이터(id, title, musicRrl, coverUrl, genre, singer)가 모두 NULL인지 여부")
						)
						.build()
				)
			));
	}

	@Test
	void 게시글이_존재하지_않으면_수정할_수_없다() throws Exception {
		Long postId = 1L;
		PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder()
			.musicId("1")
			.title("title")
			.musicUrl("musicUrl")
			.albumCoverUrl("albumConverUrl")
			.genre(Genre.EDM)
			.singer("singer")
			.battlePossible(true)
			.content("content")
			.build();

		doThrow(new EntityNotFoundException(NOT_FOUND_POST.getMessage())).when(postService)
			.update(any(), anyLong(), any());
		ResultActions actions = mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken")
			.content(mapper.writeValueAsString(postUpdateRequestDto))
			.with(csrf())
		);

		verify(postService).update(any(), anyLong(), any());
		actions
			.andExpect(status().isNotFound())
			.andDo(document("게시물 수정 실패 - 게시글이 존재하지 않는 경우",
				resource(
					ResourceSnippetParameters.builder().tag(POST_API_NAME)
						.description("게시물 수정 API")
						.requestHeaders(
							headerWithName("Authorization").description("Hype 서비스 Access Token")
						)
						.pathParameters(
							parameterWithName("postId").description("조회할 공유 게시글 id")
						)
						.requestFields(
							fieldWithPath("musicId").type(STRING).description("수정할 음악 id"),
							fieldWithPath("title").type(STRING).description("수정할 음악 제목"),
							fieldWithPath("musicUrl").type(STRING).description("수정할 음악 재생 url"),
							fieldWithPath("albumCoverUrl").type(STRING).description("수정할 음악 앨범 커버 url"),
							fieldWithPath("genre").type(STRING).description("수정할 음악 장르"),
							fieldWithPath("singer").type(STRING).description("수정할 음악 가수 이름"),
							fieldWithPath("battlePossible").type(BOOLEAN).description("수정할 게시글 대결 가능 여부"),
							fieldWithPath("content").type(STRING).description("수정할 게시글 한마디 내용"),
							fieldWithPath("allDataNull").type(BOOLEAN).description("모든 데이터가 전부 NULL인지 여부"),
							fieldWithPath("musicDataNull").type(BOOLEAN)
								.description("음악 정보 데이터(id, title, musicRrl, coverUrl, genre, singer)가 모두 NULL인지 여부")
						)
						.responseFields(
							fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
							fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
							fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
						)
						.build()
				)
			));
	}

	@Test
	void 수정하는_유저가_존재하지_않으면_수정할_수_없다() throws Exception {
		Long postId = 1L;
		PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder()
			.musicId("1")
			.title("title")
			.musicUrl("musicUrl")
			.albumCoverUrl("albumConverUrl")
			.genre(Genre.EDM)
			.singer("singer")
			.battlePossible(true)
			.content("content")
			.build();

		doThrow(new EntityNotFoundException(NOT_FOUND_MEMBER.getMessage())).when(postService)
			.update(any(), anyLong(), any());
		ResultActions actions = mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken")
			.content(mapper.writeValueAsString(postUpdateRequestDto))
			.with(csrf())
		);

		verify(postService).update(any(), anyLong(), any());
		actions
			.andExpect(status().isNotFound())
			.andDo(document("게시물 수정 실패 - 수정하려는 유저가 존재하지 않는 경우",
				resource(
					ResourceSnippetParameters.builder().tag(POST_API_NAME)
						.description("게시물 수정 API")
						.requestHeaders(
							headerWithName("Authorization").description("Hype 서비스 Access Token")
						)
						.pathParameters(
							parameterWithName("postId").description("조회할 공유 게시글 id")
						)
						.requestFields(
							fieldWithPath("musicId").type(STRING).description("수정할 음악 id"),
							fieldWithPath("title").type(STRING).description("수정할 음악 제목"),
							fieldWithPath("musicUrl").type(STRING).description("수정할 음악 재생 url"),
							fieldWithPath("albumCoverUrl").type(STRING).description("수정할 음악 앨범 커버 url"),
							fieldWithPath("genre").type(STRING).description("수정할 음악 장르"),
							fieldWithPath("singer").type(STRING).description("수정할 음악 가수 이름"),
							fieldWithPath("battlePossible").type(BOOLEAN).description("수정할 게시글 대결 가능 여부"),
							fieldWithPath("content").type(STRING).description("수정할 게시글 한마디 내용"),
							fieldWithPath("allDataNull").type(BOOLEAN).description("모든 데이터가 전부 NULL인지 여부"),
							fieldWithPath("musicDataNull").type(BOOLEAN)
								.description("음악 정보 데이터(id, title, musicRrl, coverUrl, genre, singer)가 모두 NULL인지 여부")
						)
						.responseFields(
							fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
							fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
							fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
						)
						.build()
				)
			));
	}

	@Test
	void 게시글의_게시자가_아니면_수정할_수_없다() throws Exception {
		Long postId = 1L;
		PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder()
			.musicId("1")
			.title("title")
			.musicUrl("musicUrl")
			.albumCoverUrl("albumConverUrl")
			.genre(Genre.EDM)
			.singer("singer")
			.battlePossible(true)
			.content("content")
			.build();

		doThrow(new IllegalArgumentException(NOT_POST_OWNER.getMessage())).when(postService)
			.update(any(), anyLong(), any());
		ResultActions actions = mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken")
			.content(mapper.writeValueAsString(postUpdateRequestDto))
			.with(csrf())
		);

		verify(postService).update(any(), anyLong(), any());
		actions
			.andExpect(status().isBadRequest())
			.andDo(document("게시물 수정 실패 - 게시글의 게시자가 아닌 경우",
				resource(
					ResourceSnippetParameters.builder().tag(POST_API_NAME)
						.description("게시물 수정 API")
						.requestHeaders(
							headerWithName("Authorization").description("Hype 서비스 Access Token")
						)
						.pathParameters(
							parameterWithName("postId").description("조회할 공유 게시글 id")
						)
						.requestFields(
							fieldWithPath("musicId").type(STRING).description("수정할 음악 id"),
							fieldWithPath("title").type(STRING).description("수정할 음악 제목"),
							fieldWithPath("musicUrl").type(STRING).description("수정할 음악 재생 url"),
							fieldWithPath("albumCoverUrl").type(STRING).description("수정할 음악 앨범 커버 url"),
							fieldWithPath("genre").type(STRING).description("수정할 음악 장르"),
							fieldWithPath("singer").type(STRING).description("수정할 음악 가수 이름"),
							fieldWithPath("battlePossible").type(BOOLEAN).description("수정할 게시글 대결 가능 여부"),
							fieldWithPath("content").type(STRING).description("수정할 게시글 한마디 내용"),
							fieldWithPath("allDataNull").type(BOOLEAN).description("모든 데이터가 전부 NULL인지 여부"),
							fieldWithPath("musicDataNull").type(BOOLEAN)
								.description("음악 정보 데이터(id, title, musicRrl, coverUrl, genre, singer)가 모두 NULL인지 여부")
						)
						.responseFields(
							fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
							fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
							fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
						)
						.build()
				)
			));
	}

	@Test
	void 수정할_음악_정보가_모두_null이_아니고_하나라도_null_이면_수정할_수_없다() throws Exception {
		Long postId = 1L;
		PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder()
			.musicId("1")
			.title("title")
			.musicUrl("musicUrl")
			.albumCoverUrl("albumConverUrl")
			.genre(Genre.EDM)
			.singer(null)
			.build();

		doThrow(new IllegalArgumentException("가수 이름은 Null일 수 없습니다.")).when(postService)
			.update(any(), anyLong(), any());
		ResultActions actions = mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken")
			.content(mapper.writeValueAsString(postUpdateRequestDto))
			.with(csrf())
		);

		verify(postService).update(any(), anyLong(), any());
		actions
			.andExpect(status().isBadRequest())
			.andDo(document("게시물 수정 실패 - 수정할 음악 정보가 하나라도 null인 경우 (전부 null은 상관없음)",
				resource(
					ResourceSnippetParameters.builder().tag(POST_API_NAME)
						.description("게시물 수정 API")
						.requestHeaders(
							headerWithName("Authorization").description("Hype 서비스 Access Token")
						)
						.pathParameters(
							parameterWithName("postId").description("조회할 공유 게시글 id")
						)
						.requestFields(
							fieldWithPath("musicId").type(STRING).description("수정할 음악 id"),
							fieldWithPath("title").type(STRING).description("수정할 음악 제목"),
							fieldWithPath("musicUrl").type(STRING).description("수정할 음악 재생 url"),
							fieldWithPath("albumCoverUrl").type(STRING).description("수정할 음악 앨범 커버 url"),
							fieldWithPath("genre").type(STRING).description("수정할 음악 장르"),
							fieldWithPath("singer").type(NULL).description("수정할 음악 가수 이름"),
							fieldWithPath("battlePossible").type(NULL).description("수정할 게시글 대결 가능 여부"),
							fieldWithPath("content").type(NULL).description("수정할 게시글 한마디 내용"),
							fieldWithPath("allDataNull").type(BOOLEAN).description("모든 데이터가 전부 NULL인지 여부"),
							fieldWithPath("musicDataNull").type(BOOLEAN)
								.description("음악 정보 데이터(id, title, musicRrl, coverUrl, genre, singer)가 모두 NULL인지 여부")
						)
						.responseFields(
							fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
							fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
							fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
						)
						.build()
				)
			));
	}

	@Test
	void 수정_요청된_데이터가_없다면_수정할_수_없다() throws Exception {
		Long postId = 1L;
		PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder()
			.build();

		doThrow(new IllegalArgumentException(NOT_EXIST_UPDATE_DATA.getMessage())).when(postService)
			.update(any(), anyLong(), any());
		ResultActions actions = mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken")
			.content(mapper.writeValueAsString(postUpdateRequestDto))
			.with(csrf())
		);

		verify(postService).update(any(), anyLong(), any());
		actions
			.andExpect(status().isBadRequest())
			.andDo(document("게시물 수정 실패 - 모든 수정 요청 정보가 null인 경우",
				resource(
					ResourceSnippetParameters.builder().tag(POST_API_NAME)
						.description("게시물 수정 API")
						.requestHeaders(
							headerWithName("Authorization").description("Hype 서비스 Access Token")
						)
						.pathParameters(
							parameterWithName("postId").description("조회할 공유 게시글 id")
						)
						.requestFields(
							fieldWithPath("musicId").type(NULL).description("수정할 음악 id"),
							fieldWithPath("title").type(NULL).description("수정할 음악 제목"),
							fieldWithPath("musicUrl").type(NULL).description("수정할 음악 재생 url"),
							fieldWithPath("albumCoverUrl").type(NULL).description("수정할 음악 앨범 커버 url"),
							fieldWithPath("genre").type(NULL).description("수정할 음악 장르"),
							fieldWithPath("singer").type(NULL).description("수정할 음악 가수 이름"),
							fieldWithPath("battlePossible").type(NULL).description("수정할 게시글 대결 가능 여부"),
							fieldWithPath("content").type(NULL).description("수정할 게시글 한마디 내용"),
							fieldWithPath("allDataNull").type(BOOLEAN).description("모든 데이터가 전부 NULL인지 여부"),
							fieldWithPath("musicDataNull").type(BOOLEAN)
								.description("음악 정보 데이터(id, title, musicRrl, coverUrl, genre, singer)가 모두 NULL인지 여부")
						)
						.responseFields(
							fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
							fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
							fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
						)
						.build()
				)
			));
	}
}
