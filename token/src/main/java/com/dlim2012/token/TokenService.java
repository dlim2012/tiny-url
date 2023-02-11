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
    final Random random = new Random();

    private final Lock lock = new ReentrantLock();
    private int seed;

    @Autowired
    public TokenService(TokenRepository tokenRepository){
        this.tokenRepository = tokenRepository;
        seed = tokenRepository.getLastSeed().orElse(0);
        System.out.println(seed);
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
        lock.unlock();

        LocalDateTime now = LocalDateTime.now();
        System.out.println(seed);
        System.out.println(currentSeed);
        tokenRepository.save(
                new Token(currentSeed, now, now.toLocalDate().plusYears(1).plusDays(1)))
        ;

        return new TokenItem(currentSeed, now.plusSeconds(60*60*23 + random.nextInt(60*60)));
    }

}
