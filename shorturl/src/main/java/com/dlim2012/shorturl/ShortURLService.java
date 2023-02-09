package com.dlim2012.shorturl;

import com.dlim2012.clients.longurl.LongURLClient;
import com.dlim2012.clients.longurl.dto.URLPairItem;
import com.dlim2012.clients.token.TokenClient;
import com.dlim2012.shorturl.entity.LongToShortPath;
import com.dlim2012.shorturl.repository.LongToShortPathRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ShortURLService {

    private final LongToShortPathRepository longToShortPathRepository;
    private final TokenClient tokenClient;
    private final LongURLClient longURLClient;
    private final ShortURLGenerator shortURLGenerator;

    //TODO: use @Value
    private final String domain = "http://example.com";

    @Autowired
    public ShortURLService(LongToShortPathRepository longToShortPathRepository, TokenClient tokenClient, LongURLClient longURLClient) {
        this.longToShortPathRepository = longToShortPathRepository;
        this.tokenClient = tokenClient;
        this.longURLClient = longURLClient;
        this.shortURLGenerator = new ShortURLGenerator(tokenClient, domain);
    }


    public String generateShortURL(String longURL) {
        String shortPath = shortURLGenerator.generateShortURLPath();
        longToShortPathRepository.save(new LongToShortPath(shortPath, longURL));
        longURLClient.saveItem(new URLPairItem(shortPath, longURL));
        String shortURL = shortURLGenerator.shortPathToShortURL(shortPath);
        System.out.println("Generate Short Path: " + longURL + " -> " + shortURL);
        return shortPath;
    }

    public String getShortURL(String longURL) {
        LongToShortPath longToShortPath = longToShortPathRepository.findByLongURL(longURL)
                .orElseThrow(() -> new IllegalStateException("long url not found: " + longURL));
        return shortURLGenerator.shortPathToShortURL(longToShortPath.getShortURLPath());
    }
}
