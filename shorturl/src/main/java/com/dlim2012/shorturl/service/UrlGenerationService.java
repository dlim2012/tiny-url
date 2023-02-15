package com.dlim2012.shorturl.service;

import com.dlim2012.clients.token.TokenClient;
import com.dlim2012.clients.token.config.TokenConfiguration;
import com.dlim2012.clients.token.dto.TokenItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UrlGenerationService {

    private final TokenClient tokenClient;
    private final TokenConfiguration tokenConfiguration = new TokenConfiguration();

    private TokenItem tokenItem = null;
    private long seed = -1;
    private final String hostname;

    @Autowired
    public UrlGenerationService(
            TokenClient tokenClient,
            @Value("${hostname}") String hostname
    ) {
        this.tokenClient = tokenClient;
        this.hostname = hostname;
    }

    public String generateShortUrlPath(){
        if (tokenItem == null ||
                LocalDateTime.now().isAfter(tokenItem.tokenExpireTime()) ||
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

        seed += tokenConfiguration.getIncrement();
        return stringBuilder.toString();
    }

}
