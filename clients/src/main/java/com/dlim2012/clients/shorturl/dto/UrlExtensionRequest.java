package com.dlim2012.clients.shorturl.dto;

import java.time.LocalDate;

public record UrlExtensionRequest(
        LocalDate expireDate,
        String shortUrlPath,
        String longUrl,
        String userEmail,
        String text,
        Boolean isPrivate,
        Boolean saveIfNotExist
){
}
