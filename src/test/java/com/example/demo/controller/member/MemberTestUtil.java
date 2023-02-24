package com.example.demo.controller.member;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;

import lombok.AllArgsConstructor;

class MemberTestUtil {

	static Member createMember() {
		return new Member("https://hype.music/images/1",
			"nickname",
			"refreshToken",
			Social.GOOGLE,
			"socialId");
	}

	static class TestAuthentication implements Authentication {
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
	static class MemberDetails implements UserDetails {

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
