package com.example.demo.controller.member;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.example.demo.controller.TestUtil.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.controller.MemberController;
import com.example.demo.dto.member.MemberNicknameUpdateRequestDto;
import com.example.demo.dto.member.MemberUpdateResponseDto;
import com.example.demo.model.member.Member;
import com.example.demo.security.TokenAuthenticationFilter;
import com.example.demo.service.MemberService;
import com.example.demo.service.PrincipalService;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = MemberController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = TokenAuthenticationFilter.class
	)
)
@ExtendWith(MockitoExtension.class)
class MemberUpdateRestDocsTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private ObjectMapper mapper = new ObjectMapper();

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
	void ??????_?????????_?????????_????????????_?????????_???_??????() throws Exception {
		// given
		Member member = createMember();
		String fileName = "profileImage";
		String newFileUrl = "http://localhost:8080/members/profile/1/RANDOM_UUID.jpg";
		byte[] content = "test content".getBytes();
		MockMultipartFile file = new MockMultipartFile(fileName, content);

		// when
		when(memberService.updateProfileImage(any(Principal.class), any(MultipartFile.class)))
			.thenReturn(new MemberUpdateResponseDto(member.getNickname(), newFileUrl));
		ResultActions actions = mockMvc.perform(multipart("/api/v1/members/profile/image")
			.file(file)
			.header("Authorization", "Bearer accessToken")
			.with(csrf()));

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("success").value(true))
			.andExpect(jsonPath("data").exists())
			.andDo(print())
			.andDo(document("success-update-member-profile-image",
				requestHeaders(
					headerWithName("Authorization").description("HYPE ????????? Access Token")
				),
				requestParts(
					partWithName("profileImage").description("????????? ????????? ????????? ?????????")
				),
				responseHeaders(
					headerWithName("Content-type").description("?????? ????????? ??????")
				),
				responseFields(
					fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API ?????? ?????? ??????"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("API ?????? ?????? ?????????"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("API ?????? ?????? ?????????"),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("?????? ?????????"),
					fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING)
						.description("????????? ????????? ????????? URL")
				)
			));
	}

	@Test
	void ??????_?????????_????????????_?????????_???_??????() throws Exception {
		// given
		Member member = createMember();
		String newNickName = "suyoung";
		MemberNicknameUpdateRequestDto request = new MemberNicknameUpdateRequestDto(newNickName);

		// when
		when(memberService.updateNickname(any(Principal.class), anyString()))
			.thenReturn(new MemberUpdateResponseDto(newNickName, member.getProfileImageUrl()));
		ResultActions actions = mockMvc.perform(post("/api/v1/members/profile/nickname")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(request))
			.header("Authorization", "Bearer accessToken")
			.with(csrf()));

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("success").value(true))
			.andExpect(jsonPath("data").exists())
			.andDo(print())
			.andDo(document("success-update-member-profile-nickname",
				requestHeaders(
					headerWithName("Authorization").description("HYPE ????????? Access Token")
				),
				requestFields(
					fieldWithPath("nickname").description("????????? ?????????")
				),
				responseHeaders(
					headerWithName("Content-type").description("?????? ????????? ??????")
				),
				responseFields(
					fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API ?????? ?????? ??????"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("API ?????? ?????? ?????????"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("API ?????? ?????? ?????????"),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
					fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING)
						.description("????????? ????????? URL")
				)
			));
	}
}
