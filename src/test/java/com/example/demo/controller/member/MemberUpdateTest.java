package com.example.demo.controller.member;

import static com.example.demo.controller.TestUtil.*;
import static com.example.demo.util.MultipartUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.demo.common.AmazonS3ResourceStorage;
import com.example.demo.common.ApiResponse;
import com.example.demo.common.ExceptionMessage;
import com.example.demo.common.ResourceStorage;
import com.example.demo.controller.MemberController;
import com.example.demo.dto.member.MemberNicknameUpdateRequestDto;
import com.example.demo.model.member.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.MemberService;
import com.example.demo.service.PrincipalService;

@Import(TokenProvider.class)
@ExtendWith(MockitoExtension.class)
class MemberUpdateTest {

	TestRestTemplate restTemplate = new TestRestTemplate();

	@Autowired
	TokenProvider provider;

	@InjectMocks
	AmazonS3ResourceStorage resourceStorage;

	@Mock
	AmazonS3 amazonS3;

	@Mock
	MemberRepository memberRepository;

	@Test
	void 성공_프로필_이미지를_수정할_수_있다() throws MalformedURLException {
		// given
		Long entityId = 1L;
		String path = "http://s3.hype/members/profile/";
		String fileName = "test";
		String format = "jpeg";
		MockMultipartFile file = createFile(String.format("%s.%s", fileName, format));
		String expectedUrl = createSavedFileUrl(path, entityId, file);

		// when
		when(amazonS3.putObject(any(PutObjectRequest.class)))
			.thenReturn(new PutObjectResult());
		when(amazonS3.getUrl(any(), anyString()))
			.thenReturn(new URL(expectedUrl));
		String savedFileUrl = resourceStorage.save("members/profile/", 1L, file);

		// then
		assertThat(savedFileUrl.substring(savedFileUrl.lastIndexOf('.') + 1))
			.isEqualTo(format);
		assertThat(savedFileUrl).doesNotContain(fileName);
	}

	@Test
	void 성공_유저_닉네임을_수정할_수_있다() {
		// given
		PrincipalService principalService = new PrincipalService(memberRepository);
		MemberService memberService = new MemberService(principalService, mock(PostRepository.class), memberRepository,
			mock(
				ResourceStorage.class));
		MemberController memberController = new MemberController(principalService, memberService);
		String newNickname = "newNickname";
		Member member = createMember();
		MemberDetails memberDetails = new MemberDetails("1");
		UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(
			memberDetails, memberDetails.getPassword(), memberDetails.getAuthorities());

		MemberNicknameUpdateRequestDto request = new MemberNicknameUpdateRequestDto(newNickname);

		// when
		when(memberRepository.findById(anyLong()))
			.thenReturn(Optional.of(member));
		ResponseEntity<ApiResponse> apiResponse = memberController.updateNickname(principal, request);

		// then
		assertThat(apiResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	private MockMultipartFile createFile(String fileName) {
		byte[] fileContent = "test content".getBytes();
		return new MockMultipartFile("file", fileName, MediaType.IMAGE_JPEG_VALUE, fileContent);
	}

	private String createSavedFileUrl(String path, Long entityId, MultipartFile multipartFile) {
		if (Objects.isNull(multipartFile.getOriginalFilename())) {
			throw new IllegalArgumentException(ExceptionMessage.NOT_EXIST_FILE_NAME.getMessage());
		}

		return String.format("%s%s/%s.%s", path, entityId, createUniqueFilename(),
			getFormat(multipartFile.getOriginalFilename()));
	}
}
