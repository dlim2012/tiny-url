package com.dlim2012.appuser.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ShortUrlModificationResponse (
        String prevShortUrl,
        String newShortUrl,
        LocalDateTime expireDate
){
}
