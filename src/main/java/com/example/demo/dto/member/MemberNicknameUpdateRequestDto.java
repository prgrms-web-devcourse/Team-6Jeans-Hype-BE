package com.example.demo.dto.member;

import javax.validation.constraints.NotBlank;

public record MemberNicknameUpdateRequestDto(
	@NotBlank String nickname
) {
}
