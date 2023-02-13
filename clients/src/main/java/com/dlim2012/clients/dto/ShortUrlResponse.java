package com.dlim2012.clients.dto;

public record ShortUrlResponse (
    int availableShortUrl,
    String shortUrl
) {
}
