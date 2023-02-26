package com.dlim2012.appuser.dto;

public record GetProfileResponse (
    String firstname,
    String lastname,
    String email,
    Integer availableShortUrl,
    String createdAt,
    int numUrl
) {}
