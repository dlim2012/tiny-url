package com.dlim2012.clients.shorturl.dto;

public record ModifyPathRequest (
        String longUrl,
        String newShortUrlPath,
        String userEmail,
        Boolean isPrivate,
        String text
){
}
