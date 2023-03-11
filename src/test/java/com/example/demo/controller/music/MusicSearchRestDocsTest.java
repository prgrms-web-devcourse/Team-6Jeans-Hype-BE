package com.example.demo.controller.music;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.example.demo.service.MusicSearchService;

@WithMockUser
@ExtendWith(RestDocumentationExtension.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MusicSearchRestDocsTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	private MusicSearchService musicSearchService;

	@Test
	void 성공_음악을_검색할_수_있다() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(get("/api/v1/music/search")
			.contentType(MediaType.APPLICATION_JSON)
			.param("term", "아이유 좋은 날"));
		// then
		actions
			.andExpect(status().isOk())
			.andDo(document("success-music-search",
				requestParameters(
					parameterWithName("term").description("음악 검색 요청문")
				),
				responseFields(
					fieldWithPath("success").type(JsonFieldType.BOOLEAN)
						.description("API 요청 성공 여부"),
					fieldWithPath("message").type(JsonFieldType.STRING)
						.description("API 요청 응답 메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT)
						.description("API 요청 응답 데이터"),
					fieldWithPath("data.resultCount").type(JsonFieldType.NUMBER)
						.description("음악 검색 결과 개수"),
					fieldWithPath("data.results").type(JsonFieldType.ARRAY)
						.description("음악 검색 결과 데이터"),
					fieldWithPath("data.results[].wrapperType").type(JsonFieldType.STRING)
						.description("검색 결과에 대해 반환되는 객체 이름"),
					fieldWithPath("data.results[].kind").type(JsonFieldType.STRING)
						.description("검색 결과에 대한 내용의 종류"),
					fieldWithPath("data.results[].artistId").type(JsonFieldType.NUMBER)
						.description("아티스트 고유 ID"),
					fieldWithPath("data.results[].collectionId").type(JsonFieldType.NUMBER)
						.description("앨범 고유 ID"),
					fieldWithPath("data.results[].trackId").type(JsonFieldType.NUMBER)
						.description("음악 고유 ID"),
					fieldWithPath("data.results[].artistName").type(JsonFieldType.STRING)
						.description("아티스트 이름"),
					fieldWithPath("data.results[].collectionName").type(JsonFieldType.STRING)
						.description("앨범 이름"),
					fieldWithPath("data.results[].trackName").type(JsonFieldType.STRING)
						.description("음악 제목"),
					fieldWithPath("data.results[].collectionCensoredName").type(JsonFieldType.STRING)
						.description("걸러진 앨범 이름"),
					fieldWithPath("data.results[].trackCensoredName").type(JsonFieldType.STRING)
						.description("걸러진 음악 제목"),
					fieldWithPath("data.results[].artistViewUrl").type(JsonFieldType.STRING)
						.description("애플뮤직의 해당 아티스트 페이지"),
					fieldWithPath("data.results[].collectionViewUrl").type(JsonFieldType.STRING)
						.description("애플뮤직의 해당 앨범 페이지"),
					fieldWithPath("data.results[].trackViewUrl").type(JsonFieldType.STRING)
						.description("애플뮤직의 해당 음악 페이지"),
					fieldWithPath("data.results[].previewUrl").type(JsonFieldType.STRING)
						.description("음악 미리듣기 Url"),
					fieldWithPath("data.results[].artworkUrl30").type(JsonFieldType.STRING)
						.description("앨범 커버 크기 30"),
					fieldWithPath("data.results[].artworkUrl60").type(JsonFieldType.STRING)
						.description("앨범 커버 크기 60"),
					fieldWithPath("data.results[].artworkUrl100").type(JsonFieldType.STRING)
						.description("앨범 커버 크기 100"),
					fieldWithPath("data.results[].releaseDate").type(JsonFieldType.STRING)
						.description("음악 발매 일자"),
					fieldWithPath("data.results[].collectionExplicitness").type(JsonFieldType.STRING)
						.description("앨범 수집 명시성"),
					fieldWithPath("data.results[].trackExplicitness").type(JsonFieldType.STRING)
						.description("음악 수집 명시성"),
					fieldWithPath("data.results[].discCount").type(JsonFieldType.NUMBER)
						.description("앨범의 디스크 개수"),
					fieldWithPath("data.results[].discNumber").type(JsonFieldType.NUMBER)
						.description("앨범의 디스크 번호"),
					fieldWithPath("data.results[].trackCount").type(JsonFieldType.NUMBER)
						.description("앨범의 트랙 개수"),
					fieldWithPath("data.results[].trackNumber").type(JsonFieldType.NUMBER)
						.description("앨범의 트랙 번호"),
					fieldWithPath("data.results[].trackTimeMillis").type(JsonFieldType.NUMBER)
						.description("앨범 청취 시간"),
					fieldWithPath("data.results[].country").type(JsonFieldType.STRING)
						.description("나라"),
					fieldWithPath("data.results[].currency").type(JsonFieldType.STRING)
						.description("통화"),
					fieldWithPath("data.results[].primaryGenreName").type(JsonFieldType.STRING)
						.description("음악 장르 이름"),
					fieldWithPath("data.results[].isStreamable").type(JsonFieldType.BOOLEAN)
						.description("스트리밍 가능 여부")
				)));
	}

}
