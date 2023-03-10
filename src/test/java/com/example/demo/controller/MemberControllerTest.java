package com.example.demo.controller;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.example.demo.dto.member.MemberBattleGenreVO;
import com.example.demo.dto.member.MemberBattlePostVO;
import com.example.demo.dto.member.MemberBattleResponseDto;
import com.example.demo.dto.member.MemberBattlesResponseDto;
import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Post;
import com.example.demo.security.TokenAuthenticationFilter;
import com.example.demo.service.MemberService;
import com.example.demo.service.PrincipalService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	value = MemberController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = TokenAuthenticationFilter.class
	)
)
@WithMockUser
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@AutoConfigureRestDocs
class MemberControllerTest {

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private MockMvc mockMvc;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private ObjectMapper mapper = new ObjectMapper();

	@MockBean
	private MemberService memberService;

	@MockBean
	private PrincipalService principalService;

	@MockBean
	private Principal principal;

	private final Member member = createMember();

	@Test
	void ??????_?????????_?????????_??????_????????????_?????????_???_??????() throws Exception {
		// given
		MultiValueMap<String, String> queries = new LinkedMultiValueMap<>();
		queries.add("memberId", String.valueOf(0L));
		queries.add("battleStatus", String.valueOf(BattleStatus.PROGRESS));
		queries.add("genre", String.valueOf(Genre.K_POP));
		queries.add("limit", String.valueOf(5));

		when(memberService.getBattles(any(), any(), any(), any(), any()))
			.thenReturn(getMemberBattlesResponseDto());

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/api/v1/members/battles")
				.contentType(APPLICATION_JSON)
				.principal(principal)
				.params(queries)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andDo(print())
			.andDo(MockMvcRestDocumentationWrapper.document("?????? ?????? ????????? ??????",
				requestParameters(
					parameterWithName("memberId").optional().description("????????? ?????? ?????????(??????) id ???"),
					parameterWithName("battleStatus").optional().description("?????? ?????? ??? (PROGRESS or END)"),
					parameterWithName("genre").optional().description("?????? ?????? ???"),
					parameterWithName("limit").optional().description("????????? ????????? ??????")
				),
				responseFields(
					fieldWithPath("success").type(BOOLEAN).description("API ?????? ?????? ??????"),
					fieldWithPath("message").type(STRING).description("API ?????? ?????? ?????????"),
					fieldWithPath("data").type(OBJECT).description("API ?????? ?????? ?????????"),
					fieldWithPath("data.battles[]").type(ARRAY).description("????????? ?????? ?????? ?????????"),
					fieldWithPath("data.battles[].battleId").type(NUMBER).description("?????? id ???"),
					fieldWithPath("data.battles[].genre").type(OBJECT).description("?????? ?????? ??????"),
					fieldWithPath("data.battles[].genre.genreValue").type(STRING).description("?????? ???"),
					fieldWithPath("data.battles[].genre.genreName").type(STRING).description("?????????"),
					fieldWithPath("data.battles[].challenging").type(OBJECT).description("?????? ????????? ??? ??????"),
					fieldWithPath("data.battles[].challenging.title").type(STRING).description("?????? ??????"),
					fieldWithPath("data.battles[].challenging.singer").type(STRING).description("?????? ?????????"),
					fieldWithPath("data.battles[].challenging.albumUrl").type(STRING).description("?????? ?????? ????????? url"),
					fieldWithPath("data.battles[].challenging.nickname").type(STRING).description("????????? ????????? ??????"),
					fieldWithPath("data.battles[].challenging.isWin").type(BOOLEAN).description("????????? ?????? ??????"),
					fieldWithPath("data.battles[].challenged").type(OBJECT).description("?????? ?????? ?????? ??? ??????"),
					fieldWithPath("data.battles[].challenged.title").type(STRING).description("?????? ??????"),
					fieldWithPath("data.battles[].challenged.singer").type(STRING).description("?????? ?????????"),
					fieldWithPath("data.battles[].challenged.albumUrl").type(STRING).description("?????? ?????? ????????? url"),
					fieldWithPath("data.battles[].challenged.nickname").type(STRING).description("????????? ????????? ??????"),
					fieldWithPath("data.battles[].challenged.isWin").type(BOOLEAN).description("????????? ?????? ??????"),
					fieldWithPath("data.battles[].battleStatus").type(STRING).description("?????? ?????? ???")
				)
			));

		verify(memberService).getBattles(any(), any(), any(), any(), any());
	}

	private MemberBattlesResponseDto getMemberBattlesResponseDto() {
		List<Battle> battles = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			battles.add(createBattle());
		}

		List<MemberBattleResponseDto> responses = battles.stream()
			.map(battle -> {
				return MemberBattleResponseDto.builder()
					.battleId(0L)
					.genre(MemberBattleGenreVO.of(battle.getGenre()))
					.challenged(MemberBattlePostVO.of(battle.getChallengedPost().getPost(), false))
					.challenging(MemberBattlePostVO.of(battle.getChallengingPost().getPost(), false))
					.battleStatus(battle.getStatus())
					.build();
			})
			.toList();

		return MemberBattlesResponseDto.of(responses);
	}

	private Battle createBattle() {
		return Battle.builder()
			.genre(Genre.K_POP)
			.status(BattleStatus.PROGRESS)
			.challengedPost(createPost(member))
			.challengingPost(createPost(createMember()))
			.build();
	}

	private Post createPost(Member member) {
		return Post.create(
			"musicId",
			"albumCoverUrl",
			"hype",
			"musicName",
			Genre.K_POP,
			"musicUrl",
			"recommend",
			true,
			member
		);
	}

	private Member createMember() {
		return Member.builder()
			.profileImageUrl("profile")
			.nickname("name")
			.refreshToken("token")
			.socialType(Social.GOOGLE)
			.socialId("socialId")
			.build();
	}

}
