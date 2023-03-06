package com.example.demo.util;

import java.util.UUID;

public final class MultipartUtil {
	private static final String BASE_DIR = "members/profile/";

	public static String getLocalHomeDirectory() {
		return System.getProperty("user.home");
	}

	public static String getContentType(String originalName) {
		String format = getFormat(originalName).toLowerCase();
		FileFormat fileFormat = FileFormat.of(format);
		return String.format("%s/%s", fileFormat.name().toLowerCase(), format);
	}

	public static String getFormat(String originalName) {
		return originalName.substring(originalName.lastIndexOf('.') + 1);
	}

	public static String createUniqueFilename() {
		return UUID.randomUUID().toString();
	}
}
