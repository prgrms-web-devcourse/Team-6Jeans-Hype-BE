package com.example.demo.common;

import static com.example.demo.util.FileUtils.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.util.FileUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AmazonS3ResourceStorage implements ResourceStorage {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucketName;

	@Override
	public String save(String path, Long entityId, MultipartFile multipartFile) {
		String savedFilePath = getSavedFilePath(path, entityId, multipartFile);

		try {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType(FileUtils.getSmallLetterFormat(savedFilePath));
			objectMetadata.setContentLength(multipartFile.getSize());
			amazonS3.putObject(
				new PutObjectRequest(
					bucketName,
					savedFilePath,
					multipartFile.getInputStream(),
					objectMetadata
				)
			);
		} catch (IOException exception) {
			throw new IllegalStateException(ExceptionMessage.FAIL_UPLOAD_FILE_S3.getMessage());
		}

		return amazonS3.getUrl(bucketName, savedFilePath).toString();
	}
}
