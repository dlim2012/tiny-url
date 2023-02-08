package com.dlim2012.url;

import com.dlim2012.clients.token.TokenClient;
import com.dlim2012.clients.token.config.TokenConfiguration;
import com.dlim2012.clients.token.dto.TokenItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class URLGenerator {

    private final TokenClient tokenClient;
    private final TokenConfiguration tokenConfiguration;

    private TokenItem tokenItem;
    private long seed;

    @Autowired
    public URLGenerator(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
        this.tokenConfiguration = new TokenConfiguration();
        this.tokenItem = tokenClient.getToken();

        // todo: periodically delete expired urls (once every day) with low priority
    }


    public String generateShortURL(){
        seed += tokenConfiguration.getIncrement();
        if (LocalDateTime.now().isAfter(tokenItem.tokenExpireTime()) ||
                seed >= tokenConfiguration.getMaxNum()){
            tokenItem = tokenClient.getToken();
            seed = tokenItem.seed();
        }

        long num = seed;
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i< tokenConfiguration.getTokenLength(); i++){

            int remainder = (int) (num % tokenConfiguration.getCharacterMap().length());
            num /= tokenConfiguration.getCharacterMap().length();

            stringBuilder.append(tokenConfiguration.getCharacterMap().charAt(remainder));
        }

        return stringBuilder.toString();
    }
}
