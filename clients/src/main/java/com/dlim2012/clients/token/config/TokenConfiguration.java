package com.dlim2012.clients.token.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Period;

@Component
public class TokenConfiguration {
    private final int tokenLength = 7;
    private final String characterMap = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
    private final long MaxNum = (long) Math.pow(characterMap.length(), 7);
    private final int increment = 1_000_000_000;
    private final int interval = 178_937_281;
    private final Period period = Period.ofYears(1);

    public int getTokenLength() {
        return tokenLength;
    }

    public String getCharacterMap() {
        return characterMap;
    }

    public long getMaxNum() {
        return MaxNum;
    }

    public int getIncrement() {
        return increment;
    }

    public int getInterval() {
        return interval;
    }

    public Period getPeriod() {
        return period;
    }
}
