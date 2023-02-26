package com.dlim2012.appuser.dto;

public record ShortUrlModificationRequest  (
        String newShortUrlPath,
        String longUrl,
        Boolean isPrivate,
        String newDescription
) {
}
