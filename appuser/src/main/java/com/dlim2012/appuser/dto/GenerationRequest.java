package com.dlim2012.appuser.dto;

public record GenerationRequest (
        String shortUrlPath,
        String longUrl,
        boolean isPrivate,
        String description
) {
}
