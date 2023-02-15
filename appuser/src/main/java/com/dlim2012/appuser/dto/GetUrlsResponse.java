package com.dlim2012.appuser.dto;

public record GetUrlsResponse(
    String shortUrl,
    String longUrl,
    String description,
    boolean isPrivate,
    boolean isActive
) {

}
