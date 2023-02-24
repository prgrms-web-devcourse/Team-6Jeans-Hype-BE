package com.example.demo.controller.member;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.example.demo.controller.member.MemberTestUtil.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.genre.GenreVoResponseDto;
import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.dto.member.MemberPostVoResponseDto;
import com.example.demo.dto.member.MusicVoResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.MemberService;
import com.example.demo.service.PrincipalService;

@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
@WebMvcTest(MemberController.class)
public class MemberAllPostsRestDocsTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	MemberRepository memberRepository;

	@MockBean
	PrincipalService principalService;

	@MockBean
	MemberService memberService;

	@BeforeEach
	void setUp() {
		MemberDetails member = new MemberDetails("1");

		var context = SecurityContextHolder.getContext();
		context.setAuthentication(
			new UsernamePasswordAuthenticationToken(member, member.getPassword(), member.getAuthorities()));
	}

	@AfterEach
	void clear() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@WithMockUser
	public void 성공_유저가_공유한_모든_게시글을_조회할_수_있다() throws Exception {
		// given
		Member member = createMember();
		MemberAllMyPostsResponseDto response = createResponse(member);

		// when
		when(memberRepository.findById(anyLong()))
			.thenReturn(Optional.of(member));
		when(principalService.getMemberByPrincipal(any()))
			.thenReturn(member);
		when(memberService.getAllPosts(member))
			.thenReturn(response);

		var actions = mockMvc.perform(get("/api/v1/members/posts")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken"));
		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("success").value(true))
			.andExpect(jsonPath("data").exists())
			.andDo(print())
			.andDo(document("find-member-all-posts",
				responseFields(
					fieldWithPath("success").type(JsonFieldType.BOOLEAN)
						.description("API 요청 성공 여부"),
					fieldWithPath("message").type(JsonFieldType.STRING)
						.description("API 요청 응답 메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT)
						.description("API 요청 응답 데이터"),
					fieldWithPath("data.myPosts").type(JsonFieldType.ARRAY)
						.description("멤버가 공유한 게시글들"),
					fieldWithPath("data.myPosts[].postId").type(JsonFieldType.NUMBER)
						.description("게시글 ID"),
					fieldWithPath("data.myPosts[].nickname").type(JsonFieldType.STRING)
						.description("게시글을 공유한 멤버 닉네임"),
					fieldWithPath("data.myPosts[].isPossibleBattle").type(JsonFieldType.BOOLEAN)
						.description("게시글의 대결 가능 여부"),
					fieldWithPath("data.myPosts[].likeCount").type(JsonFieldType.NUMBER)
						.description("게시글이 받은 좋아요 개수"),
					fieldWithPath("data.myPosts[].music").type(JsonFieldType.OBJECT)
						.description("게시글에서 공유하는 음악 정보"),
					fieldWithPath("data.myPosts[].music.musicId").type(JsonFieldType.STRING)
						.description("게시글에서 공유하는 음악의 고유 번호"),
					fieldWithPath("data.myPosts[].music.singer").type(JsonFieldType.STRING)
						.description("게시글에서 공유하는 음악의 가수 이름"),
					fieldWithPath("data.myPosts[].music.title").type(JsonFieldType.STRING)
						.description("게시글에서 공유하는 음악의 제목"),
					fieldWithPath("data.myPosts[].music.musicUrl").type(JsonFieldType.STRING)
						.description("게시글에서 공유하는 음악의 재생 URL"),
					fieldWithPath("data.myPosts[].music.albumCoverUrl").type(JsonFieldType.STRING)
						.description("게시글에서 공유하는 음악의 앨범 커버 URL"),
					fieldWithPath("data.myPosts[].music.genre").type(JsonFieldType.OBJECT)
						.description("게시글에서 공유하는 음악의 장르"),
					fieldWithPath("data.myPosts[].music.genre.genreValue").type(JsonFieldType.STRING)
						.description("게시글에서 공유하는 음악의 장르 enum 값"),
					fieldWithPath("data.myPosts[].music.genre.genreName").type(JsonFieldType.STRING)
						.description("게시글에서 공유하는 음악의 장르명")
				)
			));
	}

	private MemberAllMyPostsResponseDto createResponse(Member member) {
		return new MemberAllMyPostsResponseDto(
			List.of(
				new MemberPostVoResponseDto(1L, member.getNickname(), true, 5,
					new MusicVoResponseDto("AB123D", "뉴진스", "Ditto", "musicUrl", "albumCoverUrl",
						new GenreVoResponseDto("K_POP", "K-POP")))
			)
		);
	}
}
