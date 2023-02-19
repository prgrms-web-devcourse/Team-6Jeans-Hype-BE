package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Image {

	@Id
	@GeneratedValue
	Long imageId;

	@NotNull
	String imageUrl;

	@OneToOne(mappedBy = "image", fetch = FetchType.LAZY)
	Member member;
}
