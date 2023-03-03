package com.dlim2012.appuser.dto;

public record DeleteUrlRequest(
        String shortUrlToDelete,
        Integer isActiveForGetUrls
){
}
