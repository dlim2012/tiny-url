package com.dlim2012.clients.shorturl.dto;

public record UrlExtensionRequest(
        String longUrl,
        String userEmail,
        Boolean isPrivate
){
}
