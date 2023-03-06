package com.example.demo.common;

import static com.example.demo.util.MultipartUtil.*;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.util.MultipartUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3ResourceStorage implements ResourceStorage {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucketName;

	@Override
	public String save(String path, Long entityId, MultipartFile multipartFile) {
		String savedFileUrl = createSavedFileUrl(path, entityId, multipartFile);

		try {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType(MultipartUtil.getFormat(savedFileUrl));
			objectMetadata.setContentLength(multipartFile.getSize());
			amazonS3.putObject(
				new PutObjectRequest(
					bucketName,
					savedFileUrl,
					multipartFile.getInputStream(),
					objectMetadata
				)
			);
		} catch (IOException exception) {
			throw new IllegalStateException(ExceptionMessage.FAIL_UPLOAD_FILE_S3.getMessage());
		}

		return amazonS3.getUrl(bucketName, savedFileUrl).toString();
	}

	private String createSavedFileUrl(String path, Long entityId, MultipartFile multipartFile) {
		if (Objects.isNull(multipartFile.getOriginalFilename())) {
			throw new IllegalArgumentException(ExceptionMessage.NOT_EXIST_FILE_NAME.getMessage());
		}

		return String.format("%s%s/%s.%s", path, entityId, createUniqueFilename(),
			getFormat(multipartFile.getOriginalFilename()));
	}
}
