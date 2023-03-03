package com.dlim2012.appuser.service;

import com.dlim2012.clients.token.TokenClient;
import com.dlim2012.clients.token.config.TokenConfiguration;
import com.dlim2012.clients.token.dto.TokenItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class UrlGenerationService {

    private final TokenClient tokenClient;
    private final TokenConfiguration tokenConfiguration = new TokenConfiguration();

    private TokenItem tokenItem = null;
    private long seed = -1;
    private final String hostname;
    private final Lock lock = new ReentrantLock();

    @Autowired
    public UrlGenerationService(
            TokenClient tokenClient,
            @Value("${hostname}") String hostname
    ) throws InterruptedException {
        this.tokenClient = tokenClient;
        this.hostname = hostname;
        while (tokenItem == null) {
            try {
                refreshToken();
            } catch (Exception e) {
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

    public void refreshToken(){
        tokenItem = tokenClient.getToken();
        seed = tokenItem.seed();
    }

    public String generateShortUrlPath(){
        lock.lock();
        if (LocalDateTime.now().isAfter(tokenItem.tokenExpireTime())){
            refreshToken();
        }

        long num = seed;
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i< tokenConfiguration.getTokenLength(); i++){

            int remainder = (int) (num % tokenConfiguration.getCharacterMap().length());
            num /= tokenConfiguration.getCharacterMap().length();

            stringBuilder.append(tokenConfiguration.getCharacterMap().charAt(remainder));
        }

        seed += tokenConfiguration.getIncrement();


        if (seed >= tokenConfiguration.getMaxNum()){
            refreshToken();
        }
        lock.unlock();
        return stringBuilder.toString();
    }

}
