package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
	@GetMapping("/health")
	public ResponseEntity<Object> healthCheck(
		@RequestParam(value = "name", required = false, defaultValue = "테스트") String name) {
		return ResponseEntity.ok(name + "health");
	}
}
