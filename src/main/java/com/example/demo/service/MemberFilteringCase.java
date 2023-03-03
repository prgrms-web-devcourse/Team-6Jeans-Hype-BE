package com.example.demo.service;

import java.util.Optional;

public enum MemberFilteringCase {
	MY_PAGE,
	USER_PAGE;

	public static MemberFilteringCase getCase(Optional<Long> memberId, Long principalId) {
		if (memberId.isEmpty() || memberId.get().equals(principalId)) {
			return MY_PAGE;
		} else {
			return USER_PAGE;
		}
	}
}
