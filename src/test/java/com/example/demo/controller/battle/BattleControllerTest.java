package com.example.demo.controller.battle;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.example.demo.controller.member.MemberTestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.BattleService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;

@WithMockUser
@ExtendWith(RestDocumentationExtension.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class BattleControllerTest {
	private static final String BATTLE_API_NAME = "Battles";
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private BattleService battleService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PostRepository postRepository;
	private final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD,
		JsonAutoDetect.Visibility.ANY);

	Member firstMember = createMember("First");
	Member secondMember = createMember("Second");
	Member thirdMember = createMember("third");
	Post firstBalladPost = makePost("1", Genre.BALLAD, true, firstMember);
	Post secondBalladPost = makePost("2", Genre.BALLAD, true, secondMember);
	Post secondBalladPostSameWithFirst = makePost("1", Genre.BALLAD, true, secondMember);
	Post thirdBalladPost = makePost("10", Genre.BALLAD, true, thirdMember);
	Post firstKPopPost = makePost("3", Genre.K_POP, true, firstMember);
	Post secondKPopPost = makePost("4", Genre.K_POP, true, secondMember);

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class CreateBattle {
		@BeforeAll
		void setup() {
			memberRepository.save(firstMember);
			memberRepository.save(secondMember);
			memberRepository.save(thirdMember);
			assertThat(firstMember.getId()).isEqualTo(1L);
			assertThat(secondMember.getId()).isEqualTo(2L);

			postRepository.save(firstBalladPost);
			postRepository.save(firstKPopPost);
			postRepository.save(secondBalladPost);
			postRepository.save(secondKPopPost);
			postRepository.save(thirdBalladPost);
			postRepository.save(secondBalladPostSameWithFirst);
		}

		@Test
		@WithMockUser(username = "1")
		public void 성공_배틀생성_정상적으로_배틀생성_201() throws Exception {
			//given
			Member member = memberRepository.findById(firstMember.getId()).get();
			int startCountOfChallengedTicket = member.getCountOfChallengeTicket();
			BattleCreateRequestDto battleCreateRequestDto = BattleCreateRequestDto
				.builder()
				.challengingPostId(firstBalladPost.getId())
				.challengedPostId(secondBalladPost.getId())
				.build();

			//when
			ResultActions resultActions = mockMvc.perform(post("/api/v1/battles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(battleCreateRequestDto))
				.header("Authorization", "Bearer {AccessToken}")
				.with(csrf())
			);
			//then
			resultActions.andExpect(status().isCreated())
				.andExpect(header().string("Location", "http://localhost:8080/api/v1/battles/1"))
				.andDo(print())
				.andDo(document(
					"battle-post-success",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
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
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
							)
							.build()
					)
				));
			int endCountOfChallengeTicket = member.getCountOfChallengeTicket();
			assertThat(endCountOfChallengeTicket).isEqualTo(startCountOfChallengedTicket - 1);
		}

		@Test
		@WithMockUser(username = "1")
		public void 실패_배틀생성_배틀가능하지않거나_존재하지않는_postId로_배틀생성_404() throws Exception {
			//given

			BattleCreateRequestDto battleCreateRequestDto = BattleCreateRequestDto
				.builder()
				.challengingPostId(30L)
				.challengedPostId(secondBalladPost.getId())
				.build();

			//when
			ResultActions resultActions = mockMvc.perform(post("/api/v1/battles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(battleCreateRequestDto))
				.header("Authorization", "Bearer {AccessToken}")
				.with(csrf())
			);
			//then
			resultActions.andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document(
					"battle-post-wrong-postId",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.description("배틀 신청 API입니다.")
							.requestFields(
								fieldWithPath("challengedPostId").type(NUMBER).description("도전받는 post의 id"),
								fieldWithPath("challengingPostId").type(NUMBER).description("도전하는 post의 id")
							)
							.requestHeaders(
								headerWithName("Authorization").description("Hype 서비스 Access Token")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
							)
							.build()
					)
				));
		}

		@Test
		@WithMockUser(username = "1")
		void 실패_배틀생성_자신의_소유가_아닌_포스트로_배틀생성_400() throws Exception {
			//given
			BattleCreateRequestDto battleCreateRequestDto = BattleCreateRequestDto
				.builder()
				.challengingPostId(secondBalladPost.getId())
				.challengedPostId(thirdBalladPost.getId())
				.build();

			//when
			ResultActions resultActions = mockMvc.perform(post("/api/v1/battles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(battleCreateRequestDto))
				.header("Authorization", "Bearer {AccessToken}")
				.with(csrf())
			);
			//then
			resultActions.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document(
					"battle-challenging-post-not-mine-post",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.description("배틀 신청 API입니다.")
							.requestFields(
								fieldWithPath("challengedPostId").type(NUMBER).description("도전받는 post의 id"),
								fieldWithPath("challengingPostId").type(NUMBER).description("도전하는 post의 id")
							)
							.requestHeaders(
								headerWithName("Authorization").description("Hype 서비스 Access Token")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
							)
							.build()
					)
				));
		}

		@Test
		@WithMockUser(username = "2")
		void 실패_배틀생성_자신의_포스트를_상대로_배틀생성_400() throws Exception {
			//given

			BattleCreateRequestDto battleCreateRequestDto = BattleCreateRequestDto
				.builder()
				.challengingPostId(secondBalladPost.getId())
				.challengedPostId(secondBalladPostSameWithFirst.getId())
				.build();

			//when
			ResultActions resultActions = mockMvc.perform(post("/api/v1/battles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(battleCreateRequestDto))
				.header("Authorization", "Bearer {AccessToken}")
				.with(csrf())
			);
			//then
			resultActions.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document(
					"battle-post-challenged-post-is-mine",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.description("배틀 신청 API입니다.")
							.requestFields(
								fieldWithPath("challengedPostId").type(NUMBER).description("도전받는 post의 id"),
								fieldWithPath("challengingPostId").type(NUMBER).description("도전하는 post의 id")
							)
							.requestHeaders(
								headerWithName("Authorization").description("Hype 서비스 Access Token")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
							)
							.build()
					)
				));
		}

		@Test
		@WithMockUser(username = "1")
		void 실패_배틀생성_장르가_다른_두_포스트로_배틀_생성_400() throws Exception {
			//given

			BattleCreateRequestDto battleCreateRequestDto = BattleCreateRequestDto
				.builder()
				.challengingPostId(firstBalladPost.getId())
				.challengedPostId(secondKPopPost.getId())
				.build();

			//when
			ResultActions resultActions = mockMvc.perform(post("/api/v1/battles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(battleCreateRequestDto))
				.header("Authorization", "Bearer {AccessToken}")
				.with(csrf())
			);
			//then
			resultActions.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document(
					"battle-different-genre-post",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.description("배틀 신청 API입니다.")
							.requestFields(
								fieldWithPath("challengedPostId").type(NUMBER).description("도전받는 post의 id"),
								fieldWithPath("challengingPostId").type(NUMBER).description("도전하는 post의 id")
							)
							.requestHeaders(
								headerWithName("Authorization").description("Hype 서비스 Access Token")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
							)
							.build()
					)
				));
		}

		@Test
		@WithMockUser(username = "1")
		void 실패_배틀생성__사용자의_대결권이_충분하지않은상태로_배틀_생성_400() throws Exception {
			//given
			Member member = memberRepository.findById(firstMember.getId()).get();

			while (member.getCountOfChallengeTicket() > 0) {
				member.subtractCountOfChallengeTicket();
			}
			BattleCreateRequestDto battleCreateRequestDto = BattleCreateRequestDto
				.builder()
				.challengingPostId(firstBalladPost.getId())
				.challengedPostId(thirdBalladPost.getId())
				.build();

			//when
			ResultActions resultActions = mockMvc.perform(post("/api/v1/battles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(battleCreateRequestDto))
				.header("Authorization", "Bearer {AccessToken}")
				.with(csrf())
			);
			//then
			resultActions.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document(
					"battle-member-not-enough-ticket",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.description("배틀 신청 API입니다.")
							.requestFields(
								fieldWithPath("challengedPostId").type(NUMBER).description("도전받는 post의 id"),
								fieldWithPath("challengingPostId").type(NUMBER).description("도전하는 post의 id")
							)
							.requestHeaders(
								headerWithName("Authorization").description("Hype 서비스 Access Token")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
							)
							.build()
					)
				));
		}

		@Test
		@WithMockUser(username = "1")
		void 실패_배틀생성_같은노래에대해_배틀_생성_400() throws Exception {
			//given
			BattleCreateRequestDto battleCreateRequestDto = BattleCreateRequestDto
				.builder()
				.challengingPostId(firstBalladPost.getId())
				.challengedPostId(secondBalladPostSameWithFirst.getId())
				.build();

			//when
			ResultActions resultActions = mockMvc.perform(post("/api/v1/battles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(battleCreateRequestDto))
				.header("Authorization", "Bearer {AccessToken}")
				.with(csrf())
			);
			//then
			resultActions.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document(
					"battle-same-music-post",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.description("배틀 신청 API입니다.")
							.requestFields(
								fieldWithPath("challengedPostId").type(NUMBER).description("도전받는 post의 id"),
								fieldWithPath("challengingPostId").type(NUMBER).description("도전하는 post의 id")
							)
							.requestHeaders(
								headerWithName("Authorization").description("Hype 서비스 Access Token")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(JsonFieldType.NULL).description("API 요청 응답 메시지 - Null")
							)
							.build()
					)
				));
		}

	}

	private Post makePost(String musicId, Genre genre, boolean isBattlePossible, Member member) {
		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.musicId(musicId)
			.musicName("musicName")
			.musicUrl("musicUrl")
			.albumCoverUrl("albumCoverUrl")
			.genre(genre)
			.singer("singer")
			.isBattlePossible(isBattlePossible)
			.content("content")
			.build();
		return postCreateRequestDto.toEntity(member);
	}
}
