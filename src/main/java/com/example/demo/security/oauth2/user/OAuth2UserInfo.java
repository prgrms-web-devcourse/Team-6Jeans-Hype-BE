package com.example.demo.security.oauth2.user;

import java.util.Map;

import com.example.demo.model.member.Member;
import com.example.demo.model.member.Social;

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
	public abstract Social getSocialType();
    public abstract String getId();

    public abstract String getNickname();

    public abstract String getEmail();

    public abstract String getImageUrl();

	public Member toEntity(){
		return Member.builder()
			.nickname(getNickname())
			.profileImageUrl(getImageUrl())
			.socialId(getId())
			.socialType(getSocialType())
			.build();
	};
}
