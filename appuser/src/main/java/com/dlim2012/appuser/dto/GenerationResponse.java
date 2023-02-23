package com.dlim2012.appuser.dto;

import java.time.LocalDate;

public record GenerationResponse(
        Boolean isGenerated,
    int availableShortUrl,
    String shortUrl,
    Boolean isPrivate,
    Boolean isActive,
        LocalDate expireDate
) {
}
