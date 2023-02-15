package com.dlim2012.clients.shorturl.dto;

import java.time.LocalDate;

public record UrlGenerateRequest (
        String queryName,
        String longUrl,
        String text,
        LocalDate expireDate
){
}
