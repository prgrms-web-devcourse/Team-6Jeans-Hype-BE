package com.example.demo.controller.battle;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.demo.controller.BattleController;
import com.example.demo.dto.battle.BattleDetailsListResponseDto;
import com.example.demo.dto.battle.BattleDetailsResponseDto;
import com.example.demo.dto.battle.BattlePostResponseVo;
import com.example.demo.dto.genre.GenreVoResponseDto;
import com.example.demo.dto.member.MusicVoResponseDto;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Music;
import com.example.demo.security.TokenAuthenticationFilter;
import com.example.demo.service.BattleService;
import com.example.demo.service.PrincipalService;

@WithMockUser
@ExtendWith({MockitoExtension.class})
@AutoConfigureRestDocs
@WebMvcTest(
	value = BattleController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = TokenAuthenticationFilter.class
	)
)
public class BattleDetailsListRestDocsTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private PrincipalService principalService;

	@MockBean
	private BattleService battleService;

	@Test
	void 성공_모든_진행중인_대결들의_상세정보_리스트를_조회할_수_있다() throws Exception {
		// given
		BattleDetailsListResponseDto expected = createResponseDto();
		// when
		when(battleService.getBattleDetailsListInProgress())
			.thenReturn(expected);
		ResultActions actions = mockMvc.perform(get("/api/v1/battles")
			.contentType(MediaType.APPLICATION_JSON));
		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("success").value(true))
			.andExpect(jsonPath("data").exists())
			.andDo(document("success-find-all-battle-details-in-progress",
				responseFields(
					fieldWithPath("success").type(JsonFieldType.BOOLEAN)
						.description("API 요청 성공 여부"),
					fieldWithPath("message").type(JsonFieldType.STRING)
						.description("API 요청 응답 메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT)
						.description("API 요청 응답 데이터"),
					fieldWithPath("data.battles").type(JsonFieldType.ARRAY)
						.description("진행중인 모든 대결 상세정보 리스트"),
					fieldWithPath("data.battles[].battleId").type(JsonFieldType.NUMBER)
						.description("진행중인 대결 ID"),
					fieldWithPath("data.battles[].battleGenre").type(JsonFieldType.OBJECT)
						.description("대결의 장르"),
					fieldWithPath("data.battles[].battleGenre.genreValue").type(JsonFieldType.STRING)
						.description("대결의 장르 enum 값"),
					fieldWithPath("data.battles[].battleGenre.genreName").type(JsonFieldType.STRING)
						.description("대결의 장르명"),
					fieldWithPath("data.battles[].challenged").type(JsonFieldType.OBJECT)
						.description("대결 신청을 받은 게시물 대결 정보"),
					fieldWithPath("data.battles[].challenged.postId").type(JsonFieldType.NUMBER)
						.description("대결 신청을 받은 게시물 ID"),
					fieldWithPath("data.battles[].challenged.music").type(JsonFieldType.OBJECT)
						.description("대결 신청을 받은 게시물에서 공유한 음악 정보"),
					fieldWithPath("data.battles[].challenged.music.musicId").type(JsonFieldType.STRING)
						.description("대결 신청을 받은 게시물에서 공유한 음악의 고유 번호"),
					fieldWithPath("data.battles[].challenged.music.singer").type(JsonFieldType.STRING)
						.description("대결 신청을 받은 게시물에서 공유한 음악의 가수명"),
					fieldWithPath("data.battles[].challenged.music.title").type(JsonFieldType.STRING)
						.description("대결 신청을 받은 게시물에서 공유한 음악의 제목"),
					fieldWithPath("data.battles[].challenged.music.genre").type(JsonFieldType.OBJECT)
						.description("대결 신청을 받은 게시물에서 공유한 음악의 장르"),
					fieldWithPath("data.battles[].challenged.music.genre.genreValue").type(JsonFieldType.STRING)
						.description("대결 신청을 받은 게시물에서 공유한 음악의 장르 enum 값"),
					fieldWithPath("data.battles[].challenged.music.genre.genreName").type(JsonFieldType.STRING)
						.description("대결 신청을 받은 게시물에서 공유한 음악의 장르명"),
					fieldWithPath("data.battles[].challenged.music.musicUrl").type(JsonFieldType.STRING)
						.description("대결 신청을 받은 게시물에서 공유한 음악의 재생 URL"),
					fieldWithPath("data.battles[].challenged.music.albumCoverUrl").type(JsonFieldType.STRING)
						.description("대결 신청을 받은 게시물에서 공유한 음악의 앨범 커버 URL"),
					fieldWithPath("data.battles[].challenging").type(JsonFieldType.OBJECT)
						.description("대결을 신청한 게시물 대결 정보"),
					fieldWithPath("data.battles[].challenging.postId").type(JsonFieldType.NUMBER)
						.description("대결을 신청한 게시물 ID"),
					fieldWithPath("data.battles[].challenging.music").type(JsonFieldType.OBJECT)
						.description("대결을 신청한 게시물에서 공유한 음악 정보"),
					fieldWithPath("data.battles[].challenging.music.musicId").type(JsonFieldType.STRING)
						.description("대결을 신청한 게시물에서 공유한 음악의 고유 번호"),
					fieldWithPath("data.battles[].challenging.music.singer").type(JsonFieldType.STRING)
						.description("대결을 신청한 게시물에서 공유한 음악의 가수명"),
					fieldWithPath("data.battles[].challenging.music.title").type(JsonFieldType.STRING)
						.description("대결을 신청한 게시물에서 공유한 음악의 제목"),
					fieldWithPath("data.battles[].challenging.music.genre").type(JsonFieldType.OBJECT)
						.description("대결을 신청한 게시물에서 공유한 음악의 장르"),
					fieldWithPath("data.battles[].challenging.music.genre.genreValue").type(JsonFieldType.STRING)
						.description("대결을 신청한 게시물에서 공유한 음악의 장르 enum 값"),
					fieldWithPath("data.battles[].challenging.music.genre.genreName").type(JsonFieldType.STRING)
						.description("대결을 신청한 게시물에서 공유한 음악의 장르명"),
					fieldWithPath("data.battles[].challenging.music.musicUrl").type(JsonFieldType.STRING)
						.description("대결을 신청한 게시물에서 공유한 음악의 재생 URL"),
					fieldWithPath("data.battles[].challenging.music.albumCoverUrl").type(JsonFieldType.STRING)
						.description("대결을 신청한 게시물에서 공유한 음악의 앨범 커버 URL")
				)));
	}

	private BattleDetailsListResponseDto createResponseDto() {
		return new BattleDetailsListResponseDto(
			List.of(
				BattleDetailsResponseDto.builder()
					.battleId(1L)
					.battleGenre(GenreVoResponseDto.of(Genre.CLASSIC))
					.challenged(new BattlePostResponseVo(
						1L,
						MusicVoResponseDto.of(
							new Music(
								"ABCD1234",
								"albumCoverUrl",
								"singer",
								"title",
								Genre.CLASSIC,
								"musicUrl"
							)
						)
					))
					.challenging(new BattlePostResponseVo(
						2L,
						MusicVoResponseDto.of(
							new Music(
								"ABCD1235",
								"albumCoverUrl2",
								"singer2",
								"title2",
								Genre.CLASSIC,
								"musicUrl2"
							)
						)
					))
					.build()
			)
		);
	}
}
