package com.dlim2012.appuser.dto;

public record ExtensionRequest(
        int number,
        String shortUrl,
        boolean isPrivate
){
}
