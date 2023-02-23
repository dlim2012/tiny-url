package com.dlim2012.clients.shorturl.dto;

public record ShortUrlPathQueryRequest(
        String longUrl,
        String userEmail,
        Boolean isPrivate
) {
}
