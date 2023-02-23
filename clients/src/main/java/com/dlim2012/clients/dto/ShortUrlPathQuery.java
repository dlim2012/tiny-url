package com.dlim2012.clients.dto;

public record ShortUrlPathQuery(
        String shortUrlPath,
        String userEmail,
        Boolean isPrivate
){
}
