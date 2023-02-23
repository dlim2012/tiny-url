package com.dlim2012.clients.shorturl.dto;

import java.time.LocalDate;

public record UrlSaveRequest (
        String shortUrlPath,
        String userEmail,
        Boolean isPrivate,
        String longUrl,
        String text,
        LocalDate expireDate
) {
}
