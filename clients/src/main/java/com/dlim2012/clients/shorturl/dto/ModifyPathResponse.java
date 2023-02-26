package com.dlim2012.clients.shorturl.dto;

public record ModifyPathResponse (
    String prevShortUrlPath,
    String newShortUrlPath
){
}
