package com.dlim2012.clients.dto;

import java.time.LocalDate;

public record ShortUrlPairItem(
        String shortUrlPath,
        String longURL,
        String text,
        Boolean isPrivate,
        Boolean isActive,
        LocalDate expireDate
) {
}
