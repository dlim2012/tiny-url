package com.dlim2012.appuser.dto;

public record GenerationRequest (
        String longUrl,
        boolean isPrivate,
        String description
) {
}
