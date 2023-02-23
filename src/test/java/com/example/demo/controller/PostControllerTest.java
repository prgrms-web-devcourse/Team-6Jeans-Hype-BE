package com.example.demo.controller;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.dto.post.PostFindResponseDto;
import com.example.demo.dto.post.PostsFindResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PostController.class)
@WithMockUser
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
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
			.andDo(MockMvcRestDocumentationWrapper.document("Save Post",
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

	@Test
	void 성공_음악_공유_게시글을_장르와_대결가능여부_기준으로_조회할_수_있다() throws Exception {
		// given
		MultiValueMap<String, String> queries = new LinkedMultiValueMap<>();
		queries.add("genre", Genre.POP.toString());
		queries.add("possible", "true");

		when(postService.findAllPosts(Genre.POP, true)).thenReturn(getPostsDto());

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/api/v1/posts")
				.contentType(APPLICATION_JSON)
				.params(queries)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andDo(print())
			.andDo(MockMvcRestDocumentationWrapper.document("Find Post",
				requestParameters(
					parameterWithName("genre").description("필터링 할 장르 값 (null 가능)"),
					parameterWithName("possible").description("대결 가능 여부 (null 가능)")
				),
				responseFields(
					fieldWithPath("success").type(BOOLEAN).description("API 요청 성공 여부"),
					fieldWithPath("message").type(STRING).description("API 요청 응답 메시지"),
					fieldWithPath("data").type(OBJECT).description("API 요청 응답 메시지"),
					fieldWithPath("data.posts[]").type(ARRAY).description("조회한 공유글 정보 리스트"),
					fieldWithPath("data.posts[].postId").type(NUMBER).description("조회한 공유글 id"),
					fieldWithPath("data.posts[].music").type(OBJECT).description("조회한 공유글 음악 정보"),
					fieldWithPath("data.posts[].music.musicName").type(STRING).description("조회한 공유글 음악 제목"),
					fieldWithPath("data.posts[].music.albumCoverUrl").type(STRING)
						.description("조회한 공유글 음악 앨범 표지 이미지 url"),
					fieldWithPath("data.posts[].music.singer").type(STRING).description("조회한 공유글 음악 가수명"),
					fieldWithPath("data.posts[].music.genre").type(STRING).description("조회한 공유글 음악 장르"),
					fieldWithPath("data.posts[].likeCount").type(NUMBER).description("조회한 공유글의 좋아요 수"),
					fieldWithPath("data.posts[].isBattlePossible").type(BOOLEAN).description("조회한 공유글 대결 가능 여부"),
					fieldWithPath("data.posts[].nickname").type(STRING).description("조회한 공유글 작성자 이름")
				)
			));
	}

	private Member createMember() {

		return Member.builder()
			.profileImageUrl("profile")
			.nickname("name")
			.countOfChallengeTicket(5)
			.ranking(1)
			.victoryPoint(10)
			.victoryCount(10)
			.refreshToken("token")
			.socialType(Social.GOOGLE)
			.socialId("socialId")
			.build();
	}

	private List<Post> getPosts() {
		List<Post> posts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, Genre.DANCE, musicUrl,
				content, isPossibleBattle, createMember());
			posts.add(post);
		}
		for (int i = 0; i < 5; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, Genre.POP, musicUrl,
				content, isPossibleBattle, createMember());
			posts.add(post);
		}
		for (int i = 0; i < 5; i++) {
			Post post = Post.create(musicId, albumCoverUrl, singer, musicName, Genre.POP, musicUrl,
				content, false, createMember());
			posts.add(post);
		}
		return posts;
	}

	private PostsFindResponseDto getPostsDto() {
		List<PostFindResponseDto> postDtoList = new ArrayList<>();
		getPosts().forEach(post -> postDtoList.add(PostFindResponseDto.testFrom(post)));
		return PostsFindResponseDto.from(postDtoList);
	}

}
