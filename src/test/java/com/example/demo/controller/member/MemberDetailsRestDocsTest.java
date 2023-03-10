package com.example.demo.controller.member;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.example.demo.controller.TestUtil.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.common.ExceptionMessage;
import com.example.demo.controller.MemberController;
import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;
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
public class MemberDetailsRestDocsTest {

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
	public void ??????_??????_???????????????_?????????_???_??????() throws Exception {
		// given
		Member member = createMember();

		// when
		when(memberRepository.findById(anyLong()))
			.thenReturn(Optional.of(member));
		when(principalService.getMemberByPrincipal(any()))
			.thenReturn(member);
		var actions = mockMvc.perform(get("/api/v1/members/profile")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken"));

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("success").value(true))
			.andExpect(jsonPath("data").exists())
			.andDo(print())
			.andDo(document("success-find-my-details",
				requestParameters(
					parameterWithName("memberId").optional()
						.description("?????? ID (?????? ????????? ???????????????, ????????? memberId??? ???????????? ????????? ???????????????)")
				),
				responseFields(
					fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API ?????? ?????? ??????"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("API ?????? ?????? ?????????"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("API ?????? ?????? ?????????"),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("?????? ?????????"),
					fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("????????? ????????? URL"),
					fieldWithPath("data.ranking").type(JsonFieldType.NUMBER).description("?????? ??????"),
					fieldWithPath("data.victoryPoint").type(JsonFieldType.NUMBER).description("????????? ?????? ?????????"),
					fieldWithPath("data.victoryCount").type(JsonFieldType.NUMBER).description("?????? ??????"),
					fieldWithPath("data.countOfChanllenge").type(JsonFieldType.NUMBER).description("?????? ????????? ??????")
				)
			));
	}

	@Test
	@WithMockUser
	public void ??????_?????????_???????????????_?????????_???_??????() throws Exception {
		// given
		Member member = createMember();
		Member otherMember = createMember();

		// when
		when(memberRepository.findById(anyLong()))
			.thenReturn(Optional.of(member));
		when(principalService.getMemberByPrincipal(any()))
			.thenReturn(member);
		when(memberService.getMember(anyLong()))
			.thenReturn(otherMember);
		var actions = mockMvc.perform(get("/api/v1/members/profile")
			.contentType(MediaType.APPLICATION_JSON)
			.param("memberId", "2")
			.header("Authorization", "Bearer accessToken"));

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("success").value(true))
			.andExpect(jsonPath("data").exists())
			.andDo(print())
			.andDo(document("success-find-member-details",
				requestParameters(
					parameterWithName("memberId").optional()
						.description("?????? ID (?????? ????????? ???????????????, ????????? memberId??? ???????????? ????????? ???????????????)")
				),
				responseFields(
					fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API ?????? ?????? ??????"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("API ?????? ?????? ?????????"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("API ?????? ?????? ?????????"),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("?????? ?????????"),
					fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("????????? ????????? URL"),
					fieldWithPath("data.ranking").type(JsonFieldType.NUMBER).description("?????? ??????"),
					fieldWithPath("data.victoryPoint").type(JsonFieldType.NUMBER).description("????????? ?????? ?????????"),
					fieldWithPath("data.victoryCount").type(JsonFieldType.NUMBER).description("?????? ??????")
				)
			));
	}

	@Test
	@WithMockUser
	public void ??????_?????????_????????????_?????????_???????????????_?????????_???_??????() throws Exception {
		// given
		Member member = createMember();

		// when
		when(memberRepository.findById(anyLong()))
			.thenReturn(Optional.empty());
		when(principalService.getMemberByPrincipal(any()))
			.thenThrow(new EntityNotFoundException(ExceptionMessage.NOT_FOUND_MEMBER.getMessage()));
		var actions = mockMvc.perform(get("/api/v1/members/profile")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer accessToken"));

		// then
		actions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("success").value(false))
			.andExpect(jsonPath("data").doesNotExist())
			.andDo(print())
			.andDo(document("fail-no-user-find-member-details",
				responseFields(
					fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("API ?????? ?????? ??????"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("API ?????? ?????? ?????????"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("API ?????? ?????? ?????????")
				)
			));
	}
}
