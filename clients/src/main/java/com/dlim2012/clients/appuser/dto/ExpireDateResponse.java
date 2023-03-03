package com.dlim2012.clients.appuser.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpireDateResponse (
        String shortUrlPath,
        LocalDateTime expiresAt
) {
}
