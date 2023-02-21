package com.example.demo.model.member;

import static com.google.common.base.Preconditions.*;

import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class SocialInfo {

	@NotNull
	@Enumerated(value = EnumType.STRING)
	private Social socialType;

	@NotBlank
	private String socialId;

	SocialInfo(String socialString, String socialId) {

		checkArgument(Social.of(socialString), "해당 소셜 로그인 방식이 존재하지 않습니다.", socialString);

		checkArgument(Objects.nonNull(socialId), "소셜 ID가 Null일 수 없습니다.", socialId);
		checkArgument(socialId.isBlank(), "소셜 ID가 공백일 수 없습니다.", socialId);

		this.socialType = Social.valueOf(socialString);
		this.socialId = socialId;
	}
}
