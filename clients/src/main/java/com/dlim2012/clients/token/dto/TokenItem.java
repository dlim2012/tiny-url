package com.dlim2012.clients.token.dto;

import java.time.LocalDateTime;

public record TokenItem(
        int seed,
        LocalDateTime tokenExpireTime
) {
}
