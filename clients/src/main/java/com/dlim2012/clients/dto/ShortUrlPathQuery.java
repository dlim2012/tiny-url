package com.dlim2012.clients.dto;

import java.time.LocalDate;

public record ShortUrlPathQuery(
        String shortUrlPath,
        String userEmail,
        Boolean isPrivate,
        LocalDate expireDate
){
}
