package com.example.demo.security.oauth2.user;

import java.util.Map;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import com.example.demo.model.member.Social;

public class OAuth2UserInfoFactory {

	public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
		if (registrationId.equalsIgnoreCase(Social.GOOGLE.toString())) {
			return new GoogleOAuth2UserInfo(attributes);
		} else {
			throw new OAuth2AuthenticationException("Sorry! Login with " + registrationId + " is not supported yet.") {
			};
		}
	}
}
