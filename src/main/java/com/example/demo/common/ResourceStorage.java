package com.example.demo.common;

import org.springframework.web.multipart.MultipartFile;

public interface ResourceStorage {

	String save(String path, Long entityId, MultipartFile image);
}
