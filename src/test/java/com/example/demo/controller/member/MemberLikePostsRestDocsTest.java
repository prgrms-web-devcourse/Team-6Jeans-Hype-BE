package com.example.demo.controller.member;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

import com.example.demo.controller.MemberController;
import com.example.demo.dto.common.MusicVoResponseDto;
import com.example.demo.dto.member.MemberAllMyPostsResponseDto;
import com.example.demo.dto.member.MemberPostVoResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Like;
import com.example.demo.model.post.Music;
import com.example.demo.model.post.Post;
import com.example.demo.security.TokenAuthenticationFilter;
import com.example.demo.service.MemberService;
import com.example.demo.service.PrincipalService;

@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = MemberController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = TokenAuthenticationFilter.class
	)
)
class MemberLikePostsRestDocsTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	PrincipalService principalService;

	@MockBean
	MemberService memberService;

	@Test
	@WithMockUser
	void 성공_유저가_좋아요를_누른_모든_게시물들을_조회할_수_있다() throws Exception {
		// given
		Member liker = createMember();
		Member poster = createMember();
		Post post = createPost(poster, createMusic("musicId", Genre.EDM));
		Like like = createLike(post, liker);
		MemberAllMyPostsResponseDto expected = createResponse(List.of(post));
		// when
		when(principalService.getMemberByPrincipal(any(Principal.class)))
			.thenReturn(liker);
		when(memberService.getLikePosts(
			any(Principal.class),
			any(),
			any()
		))
			.thenReturn(expected);
		ResultActions actions = mockMvc.perform(get("/api/v1/members/likes")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer access token"));

		// then
		actions
			.andExpect(status().isOk())
			.andDo(document("success-member-likes-all",
				requestHeaders(
					headerWithName("Authorization").description("HYPE 서비스 access token")
				),
				requestParameters(
					parameterWithName("genre").optional().description("음악 장르 이름 (값을 넣지 않으면 전체 장르 조회, 넣으면 해당 장르 조회"),
					parameterWithName("limit").optional().description("응답 데이터 개수 (값을 넣지 않으면 전체 조회, 넣으면 해당 개수 조회")
				),
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

	private Member createMember() {
		return Member.builder()
			.nickname("nickname")
			.socialId("socialId")
			.refreshToken("refreshToken")
			.socialType(Social.GOOGLE)
			.profileImageUrl("profileImageUrl")
			.build();
	}

	private MemberAllMyPostsResponseDto createResponse(List<Post> posts) {
		AtomicReference<Long> id = new AtomicReference<>(1L);
		return new MemberAllMyPostsResponseDto(
			posts.stream()
				.map(post -> new MemberPostVoResponseDto(
					id.getAndSet(id.get() + 1),
					post.getMember().getNickname(),
					post.isPossibleBattle(),
					post.getLikeCount(),
					MusicVoResponseDto.of(post.getMusic())
				))
				.toList()
		);
	}

	private Music createMusic(String musicId, Genre genre) {
		return new Music(
			musicId,
			"albumCoverUrl",
			"singer",
			"title",
			genre,
			"musicUrl"
		);
	}

	private Post createPost(Member member, Music music) {
		return new Post(
			music,
			"content",
			true,
			0,
			member
		);
	}

	private Like createLike(Post post, Member liker) {
		return new Like(
			post,
			liker
		);
	}
}
