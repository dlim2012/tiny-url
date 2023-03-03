package com.dlim2012.appuser.dto;

public record SetUrlIsActiveRequest(
        String shortUrl,
        Boolean isPrivate,
        Boolean isActive
){
}
