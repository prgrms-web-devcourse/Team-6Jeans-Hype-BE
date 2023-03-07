package com.example.demo.util;

import java.util.Arrays;
import java.util.List;

import com.example.demo.common.ExceptionMessage;

import lombok.Getter;

@Getter
public enum FileFormat {
	IMAGE(List.of("gif", "jpeg", "jpg", "png", "pjpeg"));

	final List<String> formats;

	FileFormat(List<String> formats) {
		this.formats = formats;
	}

	public static FileFormat of(String format) {
		return Arrays.stream(FileFormat.values())
			.filter(v -> v.formats.contains(format))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NOT_ALLOWED_FILE_FORMAT.getMessage()));
	}
}
