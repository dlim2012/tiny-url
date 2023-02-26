package com.dlim2012.clients.appuser.dto;

import java.time.LocalDate;

public record ExpireDateResponse (
        String shortUrlPath,
        LocalDate expireDate
) {
}
