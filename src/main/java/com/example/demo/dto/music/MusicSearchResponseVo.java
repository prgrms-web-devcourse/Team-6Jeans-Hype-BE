package com.example.demo.dto.music;

public record MusicSearchResponseVo(
	String wrapperType,
	String kind,
	Long artistId,
	Long collectionId,
	Long trackId,
	String artistName,
	String collectionName,
	String trackName,
	String collectionCensoredName,
	String trackCensoredName,
	String artistViewUrl,
	String collectionViewUrl,
	String trackViewUrl,
	String previewUrl,
	String artworkUrl30,
	String artworkUrl60,
	String artworkUrl100,
	String releaseDate,
	String collectionExplicitness,
	String trackExplicitness,
	int discCount,
	int discNumber,
	int trackCount,
	int trackNumber,
	Long trackTimeMillis,
	String country,
	String currency,
	String primaryGenreName,
	Boolean isStreamable
) {
}
