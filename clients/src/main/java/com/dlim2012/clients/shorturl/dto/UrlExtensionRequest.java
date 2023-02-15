package com.dlim2012.clients.shorturl.dto;

import java.time.LocalDate;

public record UrlExtensionRequest (
        LocalDate expireDate,
        String shortUrlPath,
        String longUrl,
        String queryName
){
}
