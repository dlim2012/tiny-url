package com.dlim2012.longurl;


import com.dlim2012.clients.longurl.dto.URLPairItem;
import com.dlim2012.clients.token.config.TokenConfiguration;
import com.dlim2012.longurl.repository.ShortPathToLongRepository;
import com.dlim2012.longurl.entity.ShortPathToLong;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LongURLService {
    private final ShortPathToLongRepository shortPathToLongRepository;
    private final String domain = "http://example.com";
    private final TokenConfiguration tokenConfiguration = new TokenConfiguration();
    private final int shortURLLength = domain.length() + tokenConfiguration.getTokenLength() + 1;

    public String getShortPath(String shortURL){
        if (shortURL.length() != shortURLLength
            || !shortURL.substring(0, domain.length()).equals(domain)) {
            throw new IllegalStateException("Invalid shortURL: " + shortURL);
        }
        return shortURL.substring(domain.length());
    }

    public String getLongURL(String shortURL) {
        String shortPath = getShortPath(shortURL);
        ShortPathToLong shortPathToLong = shortPathToLongRepository.findByShortURLPath(shortPath)
                .orElseThrow(() -> new IllegalStateException("short url not found: " + shortPath));
        return shortPathToLong.getLongURL();
    }

    public void saveItem(URLPairItem urlPairItem) {
        ShortPathToLong shortPathToLong = new ShortPathToLong(urlPairItem.shortPath(), urlPairItem.longURL());
        shortPathToLongRepository.save(shortPathToLong);
    }
}
