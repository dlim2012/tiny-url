package com.dlim2012.clients.shorturl.dto;

import java.time.LocalDate;

public record UrlSaveRequest (
        String shortUrlPath,
        String queryName,
        String longUrl,
        String text,
        LocalDate expireDate
) {
}
