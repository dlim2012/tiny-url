package com.dlim2012.longurl;


import com.dlim2012.clients.longurl.dto.URLPairItem;
import com.dlim2012.clients.token.config.TokenConfiguration;
import com.dlim2012.longurl.entity.ShortPathToLong;
import com.dlim2012.longurl.repository.ShortPathToLongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Service;

@Service
public class LongUrlService {

    private final ShortPathToLongRepository shortPathToLongRepository;
    private final CassandraOperations cassandraOperations;
    private final TokenConfiguration tokenConfiguration = new TokenConfiguration();

    private final int ttl = 60 * 60 * 24 * 365;
    private final String cqlFormat = String.format(
            "insert into \"short_path_to_long\" (short_url_path, long_url) values (?, ?) USING TTL %d;",
            ttl
    );

    private final String domain;
    private final int shortURLLength;

    @Autowired
    public LongUrlService(
            ShortPathToLongRepository shortPathToLongRepository,
            CassandraOperations cassandraOperations,
            @Value("${domain}") String domain
    ) {
        this.shortPathToLongRepository = shortPathToLongRepository;
        this.cassandraOperations = cassandraOperations;
        this.domain = domain;
        shortURLLength = domain.length() + tokenConfiguration.getTokenLength() + 1;
    }


    public String getShortPath(String shortURL){
        System.out.println(shortURL);
        if (shortURL.length() != shortURLLength
            || !shortURL.substring(0, domain.length()).equals(domain)) {
            throw new IllegalStateException("Invalid shortURL: " + shortURL);
        }
        return shortURL.substring(domain.length()+1);
    }

    public String getLongUrlFromShortUrl(String shortURL) {
        String shortPath = getShortPath(shortURL);
        return getLongUrlFromShortUrlPath(shortPath);
    }

    public String getLongUrlFromShortUrlPath(String shortPath){
        ShortPathToLong shortPathToLong = shortPathToLongRepository.findByShortURLPath(shortPath)
                .orElseThrow(() -> new IllegalStateException("short url not found: " + shortPath));
        return shortPathToLong.getLongURL();
    }

    public void saveItem(URLPairItem urlPairItem) {
        cassandraOperations.getCqlOperations().execute(cqlFormat, urlPairItem.shortPath(), urlPairItem.longURL());
    }
}
