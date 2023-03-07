package com.example.demo.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.example.demo.controller.TestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

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
import com.epages.restdocs.apispec.SimpleType;
import com.example.demo.dto.battle.BattleCreateRequestDto;
import com.example.demo.dto.post.PostCreateRequestDto;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.member.Member;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.repository.BattleRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;

@WithMockUser
@ExtendWith(RestDocumentationExtension.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BattleControllerTest {
	private static final String BATTLE_API_NAME = "Battles";
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private BattleRepository battleRepository;

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

	@BeforeAll
	void setup() {
		memberRepository.save(firstMember);
		memberRepository.save(secondMember);
		memberRepository.save(thirdMember);

		assertThat(firstMember.getId()).isEqualTo(1L);
		assertThat(secondMember.getId()).isEqualTo(2L);
		assertThat(thirdMember.getId()).isEqualTo(3L);

		postRepository.save(firstBalladPost);
		postRepository.save(firstKPopPost);
		postRepository.save(secondBalladPost);
		postRepository.save(secondKPopPost);
		postRepository.save(thirdBalladPost);
		postRepository.save(secondBalladPostSameWithFirst);

		List<Battle> battles = new ArrayList<>();
		Battle progressBalladBattle = Battle.builder()
			.status(BattleStatus.PROGRESS)
			.challengedPost(firstBalladPost)
			.challengingPost(secondBalladPost)
			.genre(Genre.BALLAD)
			.build();
		Battle progressBalladBattle2 = Battle.builder()
			.status(BattleStatus.PROGRESS)
			.challengedPost(thirdBalladPost)
			.challengingPost(secondBalladPost)
			.genre(Genre.BALLAD)
			.build();
		Battle endedBalladBattle = Battle.builder()
			.status(BattleStatus.END)
			.challengedPost(firstBalladPost)
			.challengingPost(thirdBalladPost)
			.genre(Genre.BALLAD)
			.build();
		Battle progressKpopBattle = Battle.builder()
			.status(BattleStatus.PROGRESS)
			.challengedPost(firstKPopPost)
			.challengingPost(secondKPopPost)
			.genre(Genre.K_POP)
			.build();
		battles.add(progressBalladBattle);
		battles.add(progressBalladBattle2);
		battles.add(endedBalladBattle);
		battles.add(progressKpopBattle);

		battleRepository.saveAll(battles);
		assertThat(battleRepository.findAll().size()).isEqualTo(4);
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class CreateBattle {

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
				.andExpect(header().string("Location", "http://localhost:8080/api/v1/battles/5"))
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

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@WithMockUser(username = "1")
	class GetBattles {

		@Test
		public void 성공_given_없음_then_전체배틀조회_200() throws Exception {
			//given
			//when
			ResultActions resultActions = mockMvc.perform(get("/api/v1/battles")
				.header("Authorization", "Bearer {AccessToken}")
			);
			//then
			resultActions.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("data.battles").isArray())
				.andDo(document("get-battles-no-genre-no-progress",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.requestParameters(
								parameterWithName("battleStatus").optional()
									.type(SimpleType.STRING)
									.description("배틀이 진행중인지, 아닌지 넘겨줍니다. 값을 주지 않는다면 모든 배틀들을 조회합니다.\n"
										+ "PROGRESS || END"),
								parameterWithName("genre")
									.type(SimpleType.STRING)
									.optional().description("조회하고 싶은 배트들의 장르값을 넘겨줍니다. "
										+ "비어있으면 모든 장르의 배틀을 조회합니다."
										+ "HIPHOP_RAP||ROCK_METAL||INDIE_ACOUSTIC||BALLAD||TROT||K_POP||R_AND_B||"
										+ "JAZZ||J_POP||CLASSIC||EDM||POP||ETC")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(OBJECT).description("API 응답 데이터"),
								fieldWithPath("data.battles").type(JsonFieldType.ARRAY)
									.description("배틀들을 담고있는배열 입니다."),
								fieldWithPath("data.battles[].battleId").type(NUMBER)
									.description("배틀의 id 입니다."),
								fieldWithPath("data.battles[].isProgress").type(JsonFieldType.BOOLEAN)
									.description("배틀이 현재 진행중인지를 나타냅니다"),
								fieldWithPath("data.battles[].genre").type(OBJECT)
									.description("배틀의 장르 정보가 담긴 객체 입니다"),
								fieldWithPath("data.battles[].genre.genreValue").type(JsonFieldType.STRING)
									.description("장르의 값"),
								fieldWithPath("data.battles[].genre.genreName").type(JsonFieldType.STRING)
									.description("장르의 이름"),
								fieldWithPath("data.battles[].challenging").type(OBJECT)
									.description("도전 신청한 곡의 정보를 담고 있습니다."),
								fieldWithPath("data.battles[].challenging.title").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 타이틀 입니다."),
								fieldWithPath("data.battles[].challenging.singer").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 가수 입니다."),
								fieldWithPath("data.battles[].challenging.albumUrl").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 앨범 커버 이미지 입니다."),
								fieldWithPath("data.battles[].challenged").type(OBJECT)
									.description("도전 신청한 곡의 정보를 담고 있습니다."),
								fieldWithPath("data.battles[].challenged.title").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 타이틀 입니다."),
								fieldWithPath("data.battles[].challenged.singer").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 가수 입니다."),
								fieldWithPath("data.battles[].challenged.albumUrl").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 앨범 커버 이미지 입니다.")
							)
							.build()
					)
				));
		}

		@Test
		public void 성공_given_status가progress_then_진행중인_전체_배틀_조회_200() throws Exception {
			//given
			BattleStatus targetBattleStatus = BattleStatus.PROGRESS;
			//when
			ResultActions resultActions = mockMvc.perform(get("/api/v1/battles")
				.queryParam("battleStatus", targetBattleStatus.name())
				.header("Authorization", "Bearer {AccessToken}")
			);
			//then
			resultActions.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("data.battles").isArray())
				.andDo(document("get-battles-no-genre-status-progress",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.requestParameters(
								parameterWithName("battleStatus").optional()
									.type(SimpleType.STRING)
									.description("배틀이 진행중인지, 아닌지 넘겨줍니다. 값을 주지 않는다면 모든 배틀들을 조회합니다.\n"
										+ "PROGRESS || END"),
								parameterWithName("genre")
									.type(SimpleType.STRING)
									.optional().description("조회하고 싶은 배트들의 장르값을 넘겨줍니다. "
										+ "비어있으면 모든 장르의 배틀을 조회합니다."
										+ "HIPHOP_RAP||ROCK_METAL||INDIE_ACOUSTIC||BALLAD||TROT||K_POP||R_AND_B||"
										+ "JAZZ||J_POP||CLASSIC||EDM||POP||ETC")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(OBJECT).description("API 응답 데이터"),
								fieldWithPath("data.battles").type(JsonFieldType.ARRAY)
									.description("배틀들을 담고있는배열 입니다."),
								fieldWithPath("data.battles[].battleId").type(NUMBER)
									.description("배틀의 id 입니다."),
								fieldWithPath("data.battles[].isProgress").type(JsonFieldType.BOOLEAN)
									.description("배틀이 현재 진행중인지를 나타냅니다"),
								fieldWithPath("data.battles[].genre").type(OBJECT)
									.description("배틀의 장르 정보가 담긴 객체 입니다"),
								fieldWithPath("data.battles[].genre.genreValue").type(JsonFieldType.STRING)
									.description("장르의 값"),
								fieldWithPath("data.battles[].genre.genreName").type(JsonFieldType.STRING)
									.description("장르의 이름"),
								fieldWithPath("data.battles[].challenging").type(OBJECT)
									.description("도전 신청한 곡의 정보를 담고 있습니다."),
								fieldWithPath("data.battles[].challenging.title").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 타이틀 입니다."),
								fieldWithPath("data.battles[].challenging.singer").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 가수 입니다."),
								fieldWithPath("data.battles[].challenging.albumUrl").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 앨범 커버 이미지 입니다."),
								fieldWithPath("data.battles[].challenged").type(OBJECT)
									.description("도전 신청한 곡의 정보를 담고 있습니다."),
								fieldWithPath("data.battles[].challenged.title").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 타이틀 입니다."),
								fieldWithPath("data.battles[].challenged.singer").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 가수 입니다."),
								fieldWithPath("data.battles[].challenged.albumUrl").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 앨범 커버 이미지 입니다.")
							)
							.build()
					)
				));
		}

		@Test
		public void 성공_given_status가end_then_종료된_전체_배틀_조회_200() throws Exception {
			//given
			BattleStatus targetBattleStatus = BattleStatus.END;
			//when
			ResultActions resultActions = mockMvc.perform(get("/api/v1/battles")
				.queryParam("battleStatus", targetBattleStatus.name())
				.header("Authorization", "Bearer {AccessToken}")
			);
			//then
			resultActions.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("data.battles").isArray())
				.andDo(document("get-battles-no-genre-status-end",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.requestParameters(
								parameterWithName("battleStatus").optional()
									.type(SimpleType.STRING)
									.description("배틀이 진행중인지, 아닌지 넘겨줍니다. 값을 주지 않는다면 모든 배틀들을 조회합니다.\n"
										+ "PROGRESS || END"),
								parameterWithName("genre")
									.type(SimpleType.STRING)
									.optional().description("조회하고 싶은 배트들의 장르값을 넘겨줍니다. "
										+ "비어있으면 모든 장르의 배틀을 조회합니다."
										+ "HIPHOP_RAP||ROCK_METAL||INDIE_ACOUSTIC||BALLAD||TROT||K_POP||R_AND_B||"
										+ "JAZZ||J_POP||CLASSIC||EDM||POP||ETC")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(OBJECT).description("API 응답 데이터"),
								fieldWithPath("data.battles").type(JsonFieldType.ARRAY)
									.description("배틀들을 담고있는배열 입니다."),
								fieldWithPath("data.battles[].battleId").type(NUMBER)
									.description("배틀의 id 입니다."),
								fieldWithPath("data.battles[].isProgress").type(JsonFieldType.BOOLEAN)
									.description("배틀이 현재 진행중인지를 나타냅니다"),
								fieldWithPath("data.battles[].genre").type(OBJECT)
									.description("배틀의 장르 정보가 담긴 객체 입니다"),
								fieldWithPath("data.battles[].genre.genreValue").type(JsonFieldType.STRING)
									.description("장르의 값"),
								fieldWithPath("data.battles[].genre.genreName").type(JsonFieldType.STRING)
									.description("장르의 이름"),
								fieldWithPath("data.battles[].challenging").type(OBJECT)
									.description("도전 신청한 곡의 정보를 담고 있습니다."),
								fieldWithPath("data.battles[].challenging.title").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 타이틀 입니다."),
								fieldWithPath("data.battles[].challenging.singer").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 가수 입니다."),
								fieldWithPath("data.battles[].challenging.albumUrl").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 앨범 커버 이미지 입니다."),
								fieldWithPath("data.battles[].challenged").type(OBJECT)
									.description("도전 신청한 곡의 정보를 담고 있습니다."),
								fieldWithPath("data.battles[].challenged.title").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 타이틀 입니다."),
								fieldWithPath("data.battles[].challenged.singer").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 가수 입니다."),
								fieldWithPath("data.battles[].challenged.albumUrl").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 앨범 커버 이미지 입니다.")
							)
							.build()
					)
				));

		}

		@Test
		public void 성공_given_genre가ballad_then_발라드장르의_전체_배틀_조회_200() throws Exception {
			//given
			Genre targetGenre = Genre.BALLAD;
			//when
			ResultActions resultActions = mockMvc.perform(get("/api/v1/battles")
				.queryParam("genre", targetGenre.name())
				.header("Authorization", "Bearer {AccessToken}")
			);
			//then
			resultActions.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("data.battles").isArray())
				.andDo(document("get-battles-genre-ballad-no-progress",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.requestParameters(
								parameterWithName("battleStatus").optional()
									.type(SimpleType.STRING)
									.description("배틀이 진행중인지, 아닌지 넘겨줍니다. 값을 주지 않는다면 모든 배틀들을 조회합니다.\n"
										+ "PROGRESS || END"),
								parameterWithName("genre")
									.type(SimpleType.STRING)
									.optional().description("조회하고 싶은 배트들의 장르값을 넘겨줍니다. "
										+ "비어있으면 모든 장르의 배틀을 조회합니다."
										+ "HIPHOP_RAP||ROCK_METAL||INDIE_ACOUSTIC||BALLAD||TROT||K_POP||R_AND_B||"
										+ "JAZZ||J_POP||CLASSIC||EDM||POP||ETC")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(OBJECT).description("API 응답 데이터"),
								fieldWithPath("data.battles").type(JsonFieldType.ARRAY)
									.description("배틀들을 담고있는배열 입니다."),
								fieldWithPath("data.battles[].battleId").type(NUMBER)
									.description("배틀의 id 입니다."),
								fieldWithPath("data.battles[].isProgress").type(JsonFieldType.BOOLEAN)
									.description("배틀이 현재 진행중인지를 나타냅니다"),
								fieldWithPath("data.battles[].genre").type(OBJECT)
									.description("배틀의 장르 정보가 담긴 객체 입니다"),
								fieldWithPath("data.battles[].genre.genreValue").type(JsonFieldType.STRING)
									.description("장르의 값"),
								fieldWithPath("data.battles[].genre.genreName").type(JsonFieldType.STRING)
									.description("장르의 이름"),
								fieldWithPath("data.battles[].challenging").type(OBJECT)
									.description("도전 신청한 곡의 정보를 담고 있습니다."),
								fieldWithPath("data.battles[].challenging.title").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 타이틀 입니다."),
								fieldWithPath("data.battles[].challenging.singer").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 가수 입니다."),
								fieldWithPath("data.battles[].challenging.albumUrl").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 앨범 커버 이미지 입니다."),
								fieldWithPath("data.battles[].challenged").type(OBJECT)
									.description("도전 신청한 곡의 정보를 담고 있습니다."),
								fieldWithPath("data.battles[].challenged.title").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 타이틀 입니다."),
								fieldWithPath("data.battles[].challenged.singer").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 가수 입니다."),
								fieldWithPath("data.battles[].challenged.albumUrl").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 앨범 커버 이미지 입니다.")
							)
							.build()
					)
				));

		}

		@Test
		public void 성공_given_genre가kpop_then_kpop장르의_배틀_조회_200() throws Exception {
			//given
			Genre targetGenre = Genre.K_POP;
			//when
			ResultActions resultActions = mockMvc.perform(get("/api/v1/battles")
				.queryParam("genre", targetGenre.name())
				.header("Authorization", "Bearer {AccessToken}")
			);
			//then
			resultActions.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("data.battles").isArray())
				.andDo(document("get-battles-genre-kpop-no-progress",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.requestParameters(
								parameterWithName("battleStatus").optional()
									.type(SimpleType.STRING)
									.description("배틀이 진행중인지, 아닌지 넘겨줍니다. 값을 주지 않는다면 모든 배틀들을 조회합니다.\n"
										+ "PROGRESS || END"),
								parameterWithName("genre")
									.type(SimpleType.STRING)
									.optional().description("조회하고 싶은 배트들의 장르값을 넘겨줍니다. "
										+ "비어있으면 모든 장르의 배틀을 조회합니다."
										+ "HIPHOP_RAP||ROCK_METAL||INDIE_ACOUSTIC||BALLAD||TROT||K_POP||R_AND_B||"
										+ "JAZZ||J_POP||CLASSIC||EDM||POP||ETC")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(OBJECT).description("API 응답 데이터"),
								fieldWithPath("data.battles").type(JsonFieldType.ARRAY)
									.description("배틀들을 담고있는배열 입니다."),
								fieldWithPath("data.battles[].battleId").type(NUMBER)
									.description("배틀의 id 입니다."),
								fieldWithPath("data.battles[].isProgress").type(JsonFieldType.BOOLEAN)
									.description("배틀이 현재 진행중인지를 나타냅니다"),
								fieldWithPath("data.battles[].genre").type(OBJECT)
									.description("배틀의 장르 정보가 담긴 객체 입니다"),
								fieldWithPath("data.battles[].genre.genreValue").type(JsonFieldType.STRING)
									.description("장르의 값"),
								fieldWithPath("data.battles[].genre.genreName").type(JsonFieldType.STRING)
									.description("장르의 이름"),
								fieldWithPath("data.battles[].challenging").type(OBJECT)
									.description("도전 신청한 곡의 정보를 담고 있습니다."),
								fieldWithPath("data.battles[].challenging.title").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 타이틀 입니다."),
								fieldWithPath("data.battles[].challenging.singer").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 가수 입니다."),
								fieldWithPath("data.battles[].challenging.albumUrl").type(JsonFieldType.STRING)
									.description("도전 신청한 곡의 앨범 커버 이미지 입니다."),
								fieldWithPath("data.battles[].challenged").type(OBJECT)
									.description("도전 신청한 곡의 정보를 담고 있습니다."),
								fieldWithPath("data.battles[].challenged.title").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 타이틀 입니다."),
								fieldWithPath("data.battles[].challenged.singer").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 가수 입니다."),
								fieldWithPath("data.battles[].challenged.albumUrl").type(JsonFieldType.STRING)
									.description("도전 신청받은 곡의 앨범 커버 이미지 입니다.")
							)
							.build()
					)
				));
		}

		@Test
		public void 성공_given_progess가_end고_genre가kpop_then_kpop장르의_종료된_배틀_조회_200() throws Exception {
			//given
			Genre targetGenre = Genre.K_POP;
			BattleStatus targetBattleStatus = BattleStatus.END;
			//when
			ResultActions resultActions = mockMvc.perform(get("/api/v1/battles")
				.queryParam("genre", targetGenre.name())
				.queryParam("battleStatus", targetBattleStatus.name())
				.header("Authorization", "Bearer {AccessToken}")
			);
			//then
			resultActions.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("data.battles").isArray())
				.andDo(document("get-battles-genre-kpop-status-end",
					resource(
						ResourceSnippetParameters.builder().tag(BATTLE_API_NAME)
							.requestParameters(
								parameterWithName("battleStatus").optional()
									.type(SimpleType.STRING)
									.description("배틀이 진행중인지, 아닌지 넘겨줍니다. 값을 주지 않는다면 모든 배틀들을 조회합니다.\n"
										+ "PROGRESS || END"),
								parameterWithName("genre")
									.type(SimpleType.STRING)
									.optional().description("조회하고 싶은 배트들의 장르값을 넘겨줍니다. "
										+ "비어있으면 모든 장르의 배틀을 조회합니다."
										+ "HIPHOP_RAP||ROCK_METAL||INDIE_ACOUSTIC||BALLAD||TROT||K_POP||R_AND_B||"
										+ "JAZZ||J_POP||CLASSIC||EDM||POP||ETC")
							).responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING).description("API 요청 응답 메시지"),
								fieldWithPath("data").type(OBJECT).description("API 응답 데이터"),
								fieldWithPath("data.battles").type(JsonFieldType.ARRAY)
									.description("배틀들을 담고있는배열 입니다.")
							)
							.build()
					)
				));

		}
	}

	@Nested
	@WithMockUser(username = "1")
	class GetBattleDetailById {
		@Test
		public void 성공_given_끝나지않은_battleId_then_배틀의_상세정보_200() throws Exception {
			//given
			List<Battle> battlesStatusIsProgress = battleRepository.findAllByStatusEquals(BattleStatus.PROGRESS);
			Long targetBattleId = battlesStatusIsProgress.get(0).getId();
			//when
			ResultActions resultActions = mockMvc
				.perform(get("/api/v1/battles/{battleId}", targetBattleId)
					.header("Authorization", "Bearer accessToken"));
			//then
			resultActions.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("not-ended-battle-detail",
					resource(
						ResourceSnippetParameters.builder()
							.tag(BATTLE_API_NAME)
							.requestHeaders(
								headerWithName("Authorization").description("HYPE 서비스 access token")
							)
							.pathParameters(
								parameterWithName("battleId").type(SimpleType.NUMBER)
									.description("대결 ID"))
							.responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN)
									.description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING)
									.description("API 요청 응답 메시지"),
								fieldWithPath("data").type(JsonFieldType.OBJECT)
									.description("API 요청 응답 데이터"),
								fieldWithPath("data.battleId").type(JsonFieldType.NUMBER).description("대결 ID"),
								fieldWithPath("data.isProgress").type(JsonFieldType.BOOLEAN).description("진행중인지 여부"),
								fieldWithPath("data.isVoted").type(JsonFieldType.BOOLEAN).description("대결 투표 여부"),
								fieldWithPath("data.battleGenre").type(JsonFieldType.OBJECT).description("대결의 장르"),
								fieldWithPath("data.battleGenre.genreValue").type(JsonFieldType.STRING)
									.description("대결의 장르 enum 값"),
								fieldWithPath("data.battleGenre.genreName").type(JsonFieldType.STRING)
									.description("대결의 장르명"),
								fieldWithPath("data.challenged").type(JsonFieldType.OBJECT)
									.description("대결 신청을 받은 게시물 대결 정보"),
								fieldWithPath("data.challenged.postId").type(JsonFieldType.NUMBER)
									.description("대결 신청을 받은 게시물 ID"),
								fieldWithPath("data.challenged.voteCnt").type(JsonFieldType.NUMBER).optional()
									.description("대결 신청 받은 게시물의 득표수 - optinal(isProgress가 true면 없음)"),
								fieldWithPath("data.challenged.music").type(JsonFieldType.OBJECT)
									.description("대결 신청을 받은 게시물에서 공유한 음악 정보"),
								fieldWithPath("data.challenged.music.musicId").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 고유 번호"),
								fieldWithPath("data.challenged.music.singer").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 가수명"),
								fieldWithPath("data.challenged.music.title").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 제목"),
								fieldWithPath("data.challenged.music.genre").type(JsonFieldType.OBJECT)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 장르"),
								fieldWithPath("data.challenged.music.genre.genreValue").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 장르 enum 값"),
								fieldWithPath("data.challenged.music.genre.genreName").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 장르명"),
								fieldWithPath("data.challenged.music.musicUrl").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 재생 URL"),
								fieldWithPath("data.challenged.music.albumCoverUrl").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 앨범 커버 URL"),

								fieldWithPath("data.challenging").type(JsonFieldType.OBJECT)
									.description("대결을 신청한 게시물 대결 정보"),
								fieldWithPath("data.challenging.postId").type(JsonFieldType.NUMBER)
									.description("대결을 신청한 게시물 ID"),
								fieldWithPath("data.challenging.voteCnt").type(JsonFieldType.NUMBER).optional()
									.description("대결을 신청한 게시물의 득표수 - optinal(isProgress가 true면 없음)"),
								fieldWithPath("data.challenging.music").type(JsonFieldType.OBJECT)
									.description("대결을 신청한 게시물에서 공유한 음악 정보"),
								fieldWithPath("data.challenging.music.musicId").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 고유 번호"),
								fieldWithPath("data.challenging.music.singer").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 가수명"),
								fieldWithPath("data.challenging.music.title").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 제목"),
								fieldWithPath("data.challenging.music.genre").type(JsonFieldType.OBJECT)
									.description("대결을 신청한 게시물에서 공유한 음악의 장르"),
								fieldWithPath("data.challenging.music.genre.genreValue").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 장르 enum 값"),
								fieldWithPath("data.challenging.music.genre.genreName").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 장르명"),
								fieldWithPath("data.challenging.music.musicUrl").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 재생 URL"),
								fieldWithPath("data.challenging.music.albumCoverUrl").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 앨범 커버 URL")

							)
							.build()
					)
				));
		}

		@Test
		public void 성공_given_이미끝난_battleId_then_그_배틀의_상세정보_및_투표수_200() throws Exception {
			//given
			List<Battle> battlesStatusIsProgress = battleRepository.findAllByStatusEquals(BattleStatus.END);
			Long targetBattleId = battlesStatusIsProgress.get(0).getId();
			//when
			ResultActions resultActions = mockMvc
				.perform(get("/api/v1/battles/{battleId}", targetBattleId)
					.header("Authorization", "Bearer accessToken"));
			//then
			resultActions.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("ended-battle-detail",
					resource(
						ResourceSnippetParameters.builder()
							.tag(BATTLE_API_NAME)
							.requestHeaders(
								headerWithName("Authorization").description("HYPE 서비스 access token")
							)
							.pathParameters(
								parameterWithName("battleId").type(SimpleType.NUMBER)
									.description("대결 ID"))
							.responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN)
									.description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING)
									.description("API 요청 응답 메시지"),
								fieldWithPath("data").type(JsonFieldType.OBJECT)
									.description("API 요청 응답 데이터"),
								fieldWithPath("data.battleId").type(JsonFieldType.NUMBER).description("대결 ID"),
								fieldWithPath("data.isProgress").type(JsonFieldType.BOOLEAN).description("진행중인지 여부"),
								fieldWithPath("data.isVoted").type(JsonFieldType.BOOLEAN).description("대결 투표 여부"),
								fieldWithPath("data.battleGenre").type(JsonFieldType.OBJECT).description("대결의 장르"),
								fieldWithPath("data.battleGenre.genreValue").type(JsonFieldType.STRING)
									.description("대결의 장르 enum 값"),
								fieldWithPath("data.battleGenre.genreName").type(JsonFieldType.STRING)
									.description("대결의 장르명"),
								fieldWithPath("data.battleCreatedDate").type(JsonFieldType.STRING)
									.description("배틀이 생성된 날짜"),

								fieldWithPath("data.challenged").type(JsonFieldType.OBJECT)
									.description("대결 신청을 받은 게시물 대결 정보"),
								fieldWithPath("data.challenged.postId").type(JsonFieldType.NUMBER)
									.description("대결 신청을 받은 게시물 ID"),
								fieldWithPath("data.challenged.voteCnt").type(NUMBER).optional()
									.description("대결 신청 받은 게시물의 득표수 - optinal(isProgress가 true면 없음)"),
								fieldWithPath("data.challenged.music").type(JsonFieldType.OBJECT)
									.description("대결 신청을 받은 게시물에서 공유한 음악 정보"),
								fieldWithPath("data.challenged.music.musicId").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 고유 번호"),
								fieldWithPath("data.challenged.music.singer").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 가수명"),
								fieldWithPath("data.challenged.music.title").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 제목"),
								fieldWithPath("data.challenged.music.genre").type(JsonFieldType.OBJECT)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 장르"),
								fieldWithPath("data.challenged.music.genre.genreValue").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 장르 enum 값"),
								fieldWithPath("data.challenged.music.genre.genreName").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 장르명"),
								fieldWithPath("data.challenged.music.musicUrl").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 재생 URL"),
								fieldWithPath("data.challenged.music.albumCoverUrl").type(JsonFieldType.STRING)
									.description("대결 신청을 받은 게시물에서 공유한 음악의 앨범 커버 URL"),
								fieldWithPath("data.challenged.postWriter").type(OBJECT)
									.description("challenged post 작성 멤버"),
								fieldWithPath("data.challenged.postWriter.memberId").type(NUMBER)
									.description("challenged post 작성 멤버 id"),
								fieldWithPath("data.challenged.postWriter.nickname").type(JsonFieldType.STRING)
									.description("challenged post 작성 멤버 nickname"),

								fieldWithPath("data.challenging").type(JsonFieldType.OBJECT)
									.description("대결을 신청한 게시물 대결 정보"),
								fieldWithPath("data.challenging.postId").type(JsonFieldType.NUMBER)
									.description("대결을 신청한 게시물 ID"),
								fieldWithPath("data.challenging.voteCnt").type(NUMBER).optional()
									.description("대결을 신청한 게시물의 득표수 - optinal(isProgress가 true면 없음)"),
								fieldWithPath("data.challenging.music").type(JsonFieldType.OBJECT)
									.description("대결을 신청한 게시물에서 공유한 음악 정보"),
								fieldWithPath("data.challenging.music.musicId").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 고유 번호"),
								fieldWithPath("data.challenging.music.singer").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 가수명"),
								fieldWithPath("data.challenging.music.title").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 제목"),
								fieldWithPath("data.challenging.music.genre").type(JsonFieldType.OBJECT)
									.description("대결을 신청한 게시물에서 공유한 음악의 장르"),
								fieldWithPath("data.challenging.music.genre.genreValue").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 장르 enum 값"),
								fieldWithPath("data.challenging.music.genre.genreName").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 장르명"),
								fieldWithPath("data.challenging.music.musicUrl").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 재생 URL"),
								fieldWithPath("data.challenging.music.albumCoverUrl").type(JsonFieldType.STRING)
									.description("대결을 신청한 게시물에서 공유한 음악의 앨범 커버 URL"),
								fieldWithPath("data.challenging.postWriter").type(OBJECT)
									.description("challenged post 작성 멤버"),
								fieldWithPath("data.challenging.postWriter.memberId").type(NUMBER)
									.description("challenged post 작성 멤버 id"),
								fieldWithPath("data.challenging.postWriter.nickname").type(JsonFieldType.STRING)
									.description("challenged post 작성 멤버 nickname")
							)
							.build()
					)
				));
		}

		// TODO: 23. 3. 4. 없는 battleId를_조회할 때
		@Test
		public void 실패_없는_battleId를_조회_404() throws Exception {
			//given
			Long targetBattleId = 300L;
			//when
			ResultActions resultActions = mockMvc
				.perform(get("/api/v1/battles/{battleId}", targetBattleId)
					.header("Authorization", "Bearer accessToken"));
			//then
			resultActions.andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document("not-found-battle-detail",
					resource(
						ResourceSnippetParameters.builder()
							.tag(BATTLE_API_NAME)
							.requestHeaders(
								headerWithName("Authorization").description("HYPE 서비스 access token")
							)
							.pathParameters(
								parameterWithName("battleId").type(SimpleType.NUMBER)
									.description("배틀id 입니다"))
							.responseFields(
								fieldWithPath("success").type(JsonFieldType.BOOLEAN)
									.description("API 요청 성공 여부"),
								fieldWithPath("message").type(JsonFieldType.STRING)
									.description("API 요청 응답 메시지"),
								fieldWithPath("data").type(NULL)
									.description("API 요청 응답 데이터 - null")

							)
							.build()
					)
				));
		}

	}

	private Post makePost(String musicId, Genre genre, boolean isBattlePossible, Member member) {
		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.musicId(musicId)
			.title("musicName")
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
