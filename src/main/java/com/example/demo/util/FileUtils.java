package com.example.demo.util;

import java.util.Objects;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.common.ExceptionMessage;

public final class FileUtils {

	public static String getSavedFilePath(String path, Long entityId, MultipartFile multipartFile) {
		if (Objects.isNull(multipartFile.getOriginalFilename())) {
			throw new IllegalArgumentException(ExceptionMessage.NOT_EXIST_FILE_NAME.getMessage());
		}

		String format = getSmallLetterFormat(multipartFile.getOriginalFilename());
		return String.format("%s%s/%s.%s", path, entityId, createUniqueFilename(), format);
	}

	public static String getSmallLetterFormat(String originalName) {
		String format = originalName.substring(originalName.lastIndexOf('.') + 1);
		FileFormat.of(format);
		return format.toLowerCase();
	}

	public static String createUniqueFilename() {
		return UUID.randomUUID().toString();
	}
}
