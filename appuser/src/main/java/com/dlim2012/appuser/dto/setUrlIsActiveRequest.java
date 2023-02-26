package com.dlim2012.appuser.dto;

public record setUrlIsActiveRequest(
        String shortUrl,
        Boolean isPrivate,
        Boolean isActive
){
}
