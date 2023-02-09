package com.dlim2012.token;

import com.dlim2012.clients.token.config.TokenConfiguration;
import com.dlim2012.clients.token.dto.TokenItem;
import com.dlim2012.token.entity.Token;
import com.dlim2012.token.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final TokenConfiguration tokenConfiguration = new TokenConfiguration();

    private final Lock lock = new ReentrantLock();

//    private final int TOKEN_LENGTH = 7;
//    private final String CHARACTER_MAP = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//    private final long MAX = (long) Math.pow(62, 7);
//    private final int INCREMENT = 1_000_000_000;
//    private final int INTERVAL = 178_937_281;
//    private final Period PERIOD = Period.ofYears(1);

    private int seed;

    @Autowired
    public TokenService(TokenRepository tokenRepository){
        this.tokenRepository = tokenRepository;
        seed = tokenRepository.getLastSeed().orElse(0);
        // todo: periodically delete expired tokens (once every day) with low priority
    }


    @GetMapping
    public TokenItem getToken() {
        lock.lock();

        Optional<com.dlim2012.token.entity.Token> tokenOptional;
        do {
            seed = (seed + tokenConfiguration.getInterval()) % tokenConfiguration.getIncrement();
            tokenOptional = tokenRepository.findById(seed);

        } while (tokenOptional.isPresent() && tokenOptional.get().getExpireDate().isAfter(LocalDate.now()));
        int currentSeed = seed;
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.save(new Token(seed, now, now.toLocalDate().plusDays(366)));
        lock.unlock();

        final Random random = new Random();
        LocalDateTime tokenExpireTime = now.plusSeconds(60*60*23 + random.nextInt(60*60));

        return new TokenItem(currentSeed, tokenExpireTime);
    }

//    public TokenConfiguration getTokenSetting() {
//        return tokenConfiguration;
//    }
//    }
}
