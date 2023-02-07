package com.dlim2012.url;

import com.dlim2012.clients.token.TokenClient;
import com.dlim2012.clients.token.dto.TokenItem;
import com.dlim2012.clients.token.dto.TokenSetting;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class URLGenerator {

    private final TokenClient tokenClient;
    private final TokenSetting tokenSetting;

    private TokenItem tokenItem;
    private long seed;

    @Autowired
    public URLGenerator(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
        this.tokenSetting = tokenClient.getTokenSetting();
        this.tokenItem = tokenClient.getToken();

        // todo: periodically delete expired urls (once every day) with low priority
    }


    public String generateShortURL(){
        seed += tokenSetting.increment();
        if (LocalDateTime.now().isAfter(tokenItem.tokenExpireTime()) ||
                seed + tokenSetting.increment() >= tokenSetting.max()){
            tokenItem = tokenClient.getToken();
            seed = tokenItem.seed();
        }

        long num = seed;
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i< tokenSetting.tokenLength(); i++){
            int remainder = (int) (num % tokenSetting.characterMap().length());
            num /= tokenSetting.characterMap().length();

            stringBuilder.append(tokenSetting.characterMap().charAt(remainder));
        }

        return stringBuilder.toString();
    }
}
