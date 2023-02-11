package com.dlim2012.shorturl;

import com.dlim2012.clients.token.TokenClient;
import com.dlim2012.clients.token.config.TokenConfiguration;
import com.dlim2012.clients.token.dto.TokenItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShortUrlGenerator {

    private final TokenClient tokenClient;
    private final TokenConfiguration tokenConfiguration = new TokenConfiguration();

    private TokenItem tokenItem;
    private long seed;
    private final String domain;

    @Autowired
    public ShortUrlGenerator(
            TokenClient tokenClient,
            @Value("${domain}") String domain
    ) {
        this.tokenClient = tokenClient;
        this.tokenItem = tokenClient.getToken();
        seed = this.tokenItem.seed();
        this.domain = domain;
    }

    public String generateShortURLPath(){
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

        seed += tokenConfiguration.getIncrement();
        return stringBuilder.toString();
    }

    public String shortPathToShortURL(String shortPath){
        return domain + "/" + shortPath;
    }
}
