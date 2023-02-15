package com.dlim2012.appuser.dto;

public record ModifyUrlRequest(
        String shortUrl,
        Boolean isPrivate
){
}
