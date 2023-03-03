package com.dlim2012.appuser.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExtensionResponse (
        boolean isExtended,
        String prevExpireDate,
        String newExpireDate,
        int remainingNumber,
        String shortUrl
) {
}
