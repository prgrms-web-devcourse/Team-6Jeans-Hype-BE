package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface MusicSearchService {
	JsonNode search(String term);
}
