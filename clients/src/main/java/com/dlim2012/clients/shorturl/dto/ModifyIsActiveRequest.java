package com.dlim2012.clients.shorturl.dto;

public record ModifyIsActiveRequest (
        String shortUrlPath,
        String userEmail,
        Boolean isPrivate,
        Boolean isActive
        ) {
}
