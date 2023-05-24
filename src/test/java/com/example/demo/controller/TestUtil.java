package com.example.demo.controller;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.model.battle.Battle;
import com.example.demo.model.battle.BattleStatus;
import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;
import com.example.demo.model.post.Genre;
import com.example.demo.model.post.Music;
import com.example.demo.model.post.Post;

import lombok.AllArgsConstructor;

public class TestUtil {

	public static Member createMember() {
		return new Member("https://hype.music/images/1",
			"nickname",
			"refreshToken",
			Social.GOOGLE,
			"socialId");
	}

	public static Member createMember(String nickname) {
		return new Member("https://hype.music/images/1",
			nickname,
			"refreshToken",
			Social.GOOGLE,
			"socialId");
	}

	public static Music createMusic() {
		return new Music(
			"musicId",
			"albumCoverUrl",
			"singer",
			"title",
			Genre.BALLAD,
			"musicUrl"
		);
	}

	public static Post createPost(Member member, Music music) {
		return Post.builder()
			.music(music)
			.content("content")
			.isPossibleBattle(true)
			.likeCount(0)
			.member(member)
			.build();
	}

	public static Battle createProgressBattle(Post post1, Post post2) {
		return new Battle(
			post1.getMusic().getGenre(),
			BattleStatus.PROGRESS,
			post1,
			post2);
	}

	public static class TestAuthentication implements Authentication {
		private final UserDetails userDetails;

		public TestAuthentication(UserDetails userDetails) {
			this.userDetails = userDetails;
		}

		@Override
		public String getName() {
			return userDetails.getUsername();
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return null;
		}

		@Override
		public Object getCredentials() {
			return null;
		}

		@Override
		public Object getDetails() {
			return null;
		}

		@Override
		public Object getPrincipal() {
			return userDetails;
		}

		@Override
		public boolean isAuthenticated() {
			return false;
		}

		@Override
		public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		}
	}

	@AllArgsConstructor
	public static class MemberDetails implements UserDetails {

		String memberId;

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return Collections.singleton(
				new SimpleGrantedAuthority("USER")
			);
		}

		@Override
		public String getPassword() {
			return "password";
		}

		@Override
		public String getUsername() {
			return memberId;
		}

		@Override
		public boolean isAccountNonExpired() {
			return false;
		}

		@Override
		public boolean isAccountNonLocked() {
			return false;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return false;
		}

		@Override
		public boolean isEnabled() {
			return false;
		}
	}
}
