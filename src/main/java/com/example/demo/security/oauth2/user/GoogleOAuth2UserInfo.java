package com.example.demo.security.oauth2.user;

import java.util.Map;

import com.example.demo.model.member.Social;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

	@Override
	public Social getSocialType() {
		return Social.GOOGLE;
	}

	@Override
    public String getNickname() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
