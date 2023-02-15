package com.dlim2012.appuser.dto;

public record ShortUrlResponse (
    int availableShortUrl,
    String shortUrl,
    Boolean isPrivate,
    Boolean isActive
) {
}
