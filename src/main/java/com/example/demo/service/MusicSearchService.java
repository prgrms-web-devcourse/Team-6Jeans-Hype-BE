package com.example.demo.service;

import com.example.demo.dto.music.MusicSearchResponseDto;

public interface MusicSearchService {
	MusicSearchResponseDto search(String term);
}
