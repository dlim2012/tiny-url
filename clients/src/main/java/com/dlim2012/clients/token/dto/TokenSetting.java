package com.dlim2012.clients.token.dto;

import java.time.Period;

public record TokenSetting(
        int tokenLength,
        String characterMap,
        long max,
        long increment,
        Period period
) {
}
