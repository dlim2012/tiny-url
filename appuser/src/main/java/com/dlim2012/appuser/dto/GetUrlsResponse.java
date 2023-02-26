package com.dlim2012.appuser.dto;

import java.time.LocalDate;

public record GetUrlsResponse(
        Integer index,
    String shortUrl,
    String longUrl,
    String description,
    String isPrivate,
    String isActive,
    LocalDate expiresAt
) {
}
