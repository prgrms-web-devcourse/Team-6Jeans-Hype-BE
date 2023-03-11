package com.example.demo.service;

import static com.example.demo.common.ExceptionMessage.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItunesMusicSearchService implements MusicSearchService {

	private final ObjectMapper objectMapper;

	@Override
	public JsonNode search(String term) {
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
			return objectMapper.readTree(response.getEntity().getContent());
		} catch (IOException exception) {
			throw new IllegalArgumentException(NOT_VALID_TERM.getMessage());
		}
	}
}
