package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Music {

	// TODO : Music을 따로 엔티티로 두는 것이 맞을지 -> 이렇게 되면 음악 검색 API 하기 이전에 쿼리를 던져야함.
	// TODO : Music 엔티티에서 musicUrl을 따로 가지고 있는게 맞을지 -> 안 가지고 있으면 매번 API를 통해 음악 재생해줘야함.

	@Id
	@GeneratedValue
	Long musicId;

	@NotNull
	String singer;

	@NotNull
	String musicName;

	@Enumerated(value = EnumType.STRING)
	Genre genre;

	@NotNull
	String musicUrl;

	@OneToMany(mappedBy = "music")
	List<Post> posts = new ArrayList<>();
}
