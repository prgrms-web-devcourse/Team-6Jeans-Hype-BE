package com.example.demo.service;

import static com.example.demo.common.ExceptionMessage.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import com.example.demo.dto.music.MusicSearchResponseDto;
import com.example.demo.dto.music.MusicSearchResponseVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItunesMusicSearchService implements MusicSearchService {

	private final ObjectMapper objectMapper;

	@Override
	public MusicSearchResponseDto search(String term) {
		HttpClient httpClient = HttpClients.createDefault();
		try {
			String requestUrl = String.format(
				"https://itunes.apple.com/search?term=%s&country=KR&media=music",
				URLEncoder.encode(term, StandardCharsets.UTF_8));

			HttpGet httpGet = new HttpGet(requestUrl);
			HttpResponse response = httpClient.execute(httpGet);
			int httpStatusCode = response.getStatusLine().getStatusCode();

			if (httpStatusCode < 200 || httpStatusCode >= 300) {
				throw new IOException(FAIL_SEARCH_MUSIC.getMessage());
			}

			JsonNode jsonNode = objectMapper.readTree(response.getEntity().getContent());
			ArrayNode results = (ArrayNode)jsonNode.get("results");
			List<MusicSearchResponseVo> convertJsonToDto = Arrays.asList(
				objectMapper.convertValue(
					results,
					MusicSearchResponseVo[].class));

			List<MusicSearchResponseVo> convertJsonToDtoNotBlankMusicUrl = convertJsonToDto.stream()
				.filter(musicSearchResponseVo ->
					Objects.nonNull(musicSearchResponseVo.previewUrl()) &&
						!musicSearchResponseVo.previewUrl().isBlank()
				)
				.toList();

			return MusicSearchResponseDto.of(
				convertJsonToDtoNotBlankMusicUrl
			);
		} catch (IOException exception) {
			throw new IllegalArgumentException(NOT_VALID_TERM.getMessage());
		}
	}
}
