package com.dlim2012.appuser.dto;

import java.time.LocalDate;

public record ExtensionResponse (
        boolean isExtended,
        LocalDate prevExpireDate,
        LocalDate newExpireDate,
        int remainingNumber
) {
}
