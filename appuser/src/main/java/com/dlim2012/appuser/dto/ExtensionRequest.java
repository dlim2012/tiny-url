package com.dlim2012.appuser.dto;

public record ExtensionRequest(
        int number,
        String ShortUrl,
        String LongUrl,
        boolean isPrivate
){
}
