package com.dlim2012.shorturl;

import com.dlim2012.clients.longurl.LongURLClient;
import com.dlim2012.clients.longurl.dto.URLPairItem;
import com.dlim2012.clients.token.TokenClient;
import com.dlim2012.shorturl.entity.LongToShortPath;
import com.dlim2012.shorturl.repository.LongToShortPathRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Slf4j
public class ShortUrlService {

    private final LongToShortPathRepository longToShortPathRepository;
    private final TokenClient tokenClient;
    private final LongURLClient longURLClient;
    private final ShortUrlGenerator shortURLGenerator;

    private final int ttl = 60 * 60 * 24 * 365;
    private final String cqlFormat = String.format(
            "insert into \"long_to_short_path\" (long_url, short_url_path) values (?, ?) USING TTL %d;",
            ttl
    );
    private final String domain;

    private CassandraOperations cassandraOperations;

    @Autowired
    public ShortUrlService(
            LongToShortPathRepository longToShortPathRepository,
            TokenClient tokenClient,
            LongURLClient longURLClient,
            CassandraOperations cassandraOperations,
            @Value("${domain}") String domain
    ) {
        this.longToShortPathRepository = longToShortPathRepository;
        this.tokenClient = tokenClient;
        this.longURLClient = longURLClient;
        this.cassandraOperations = cassandraOperations;
        this.domain = domain;

        this.shortURLGenerator = new ShortUrlGenerator(tokenClient, domain);
    }


    public String generateShortURLAndSave(String longURL) {
        Optional<LongToShortPath> longToShortPathOptional = longToShortPathRepository.findByLongURL(longURL);
        String shortPath;
        if (longToShortPathOptional.isPresent()){
            shortPath = longToShortPathOptional.get().getShortURLPath();
        } else {
            shortPath = shortURLGenerator.generateShortURLPath();
            log.info("Saving URL pair: ({}, {})", shortPath, longURL);
            cassandraOperations.getCqlOperations().execute(cqlFormat, longURL, shortPath);
            longURLClient.saveItem(new URLPairItem(shortPath, longURL));
        }
        String shortURL = shortURLGenerator.shortPathToShortURL(shortPath);
        return shortURL;
    }

    public String getShortURL(String longURL) {
        LongToShortPath longToShortPath = longToShortPathRepository.findByLongURL(longURL)
                .orElseThrow(() -> new IllegalStateException("long url not found: " + longURL));
        return shortURLGenerator.shortPathToShortURL(longToShortPath.getShortURLPath());
    }
}
