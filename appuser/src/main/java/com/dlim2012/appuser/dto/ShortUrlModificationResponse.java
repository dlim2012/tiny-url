package com.dlim2012.appuser.dto;

import java.time.LocalDate;

public record ShortUrlModificationResponse (
        String longUrl,
        String prevShortUrl,
        String newShortUrl,
        Boolean isPrivate,
        LocalDate expireDate
){
}
