package com.example.demo.model.post;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import lombok.Getter;

@Getter
@Embeddable
public class Music {

	@NotNull
	private Long id;

	@NotNull
	private String singer;

	@NotNull
	private String title;

	@Enumerated(value = EnumType.STRING)
	private Genre genre;

	@NotNull
	private String musicUrl;
}
