package com.dlim2012.clients.token.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Period;

@Component
public class TokenConfiguration {
    private final int tokenLength = 7;
    private final String characterMap = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
    private final long MaxNum = (long) Math.pow(characterMap.length(), 7);
    private final int increment = 1_000_000_000;
    private final int interval = 43_980_467;
    private final Period period = Period.ofYears(1);

    @Bean
    public int getTokenLength() {
        return tokenLength;
    }

    @Bean
    public String getCharacterMap() {
        return characterMap;
    }

    @Bean
    public long getMaxNum() {
        return MaxNum;
    }

    @Bean
    public int getIncrement() {
        return increment;
    }

    @Bean
    public int getInterval() {
        return interval;
    }

    @Bean
    public Period getPeriod() {
        return period;
    }
}
