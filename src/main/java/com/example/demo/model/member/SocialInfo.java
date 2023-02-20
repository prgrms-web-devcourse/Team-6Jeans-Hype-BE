package com.example.demo.model.member;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import lombok.Getter;

@Getter
@Embeddable
public class SocialInfo {

	@Enumerated(value = EnumType.STRING)
	private Social socialType;

	@NotNull
	private String socialId;
}
