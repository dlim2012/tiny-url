package com.dlim2012.clients.dto;

public record ShortUrlQueryResponse (
        String publicShortUrl,
        String publicDescription,
        String privateShortUrl,
        String privateDescription
) {
}
