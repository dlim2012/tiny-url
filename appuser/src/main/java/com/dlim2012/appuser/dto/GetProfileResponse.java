package com.dlim2012.appuser.dto;

import java.time.LocalDateTime;

public record GetProfileResponse (
    String firstname,
    String lastname,
    String email,
    Integer availableShortUrl,
    String createdAt
) {}
