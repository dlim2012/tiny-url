package com.dlim2012.shorturl.service;

import com.dlim2012.clients.dto.ShortUrlPairItem;
import com.dlim2012.clients.dto.ShortUrlPathItem;
import com.dlim2012.clients.dto.UrlPairItem;
import com.dlim2012.clients.token.TokenClient;
import com.dlim2012.clients.token.config.TokenConfiguration;
import com.dlim2012.shorturl.entity.StringToStringEntity;
import com.dlim2012.shorturl.repository.StringToStringRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.data.cassandra.core.cql.WriteOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ShortUrlService {

    private final StringToStringRepository stringToStringRepository;
    private final UrlGenerationService URLGenerationService;

    private final Duration ttl = Duration.ofDays(366);

    private final String hostname;
    private final TokenConfiguration tokenConfiguration = new TokenConfiguration();
    private final int shortURLLength;

    private final CassandraOperations cassandraOperations;
//    private final WriteOptions writeOptions;
    private final InsertOptions insertOptions;

    @Autowired
    public ShortUrlService(
            StringToStringRepository stringToStringRepository,
            TokenClient tokenClient,
            CassandraOperations cassandraOperations,
            @Value("${hostname}") String hostname
    ) {
        this.stringToStringRepository = stringToStringRepository;
        this.cassandraOperations = cassandraOperations;
//        this.writeOptions = WriteOptions.builder().ttl(ttl).build();
        this.insertOptions = InsertOptions.builder().ttl(ttl).build();
        System.out.println("TTL");
        System.out.println(insertOptions.getTtl());
        this.hostname = hostname;

        this.URLGenerationService = new UrlGenerationService(tokenClient, hostname);
        this.shortURLLength = hostname.length() + tokenConfiguration.getTokenLength() + 1;
    }


    public String generateShortURLAndSave(String longUrl) {
        Optional<StringToStringEntity> longToShortPathOptional = stringToStringRepository.findById(longUrl);
        String shortPath;
        if (longToShortPathOptional.isPresent()){
            shortPath = longToShortPathOptional.get().getValue();
            // todo: extend time
            cassandraOperations.insert(new StringToStringEntity(longUrl, shortPath), insertOptions);
            cassandraOperations.insert(new StringToStringEntity(shortPath, longUrl), insertOptions);
            log.info("Short URL for {} already exists: {}", longUrl, shortPath);
        } else {
            shortPath = URLGenerationService.generateShortURLPath();
            log.info("Short URL generated for {}: {}", longUrl, shortPath);
            log.info("Saving URL pair: ({}, {})", shortPath, longUrl);
//            cassandraOperations.getCqlOperations().execute(cqlInsertFormat, longUrl, shortPath);
//            cassandraOperations.getCqlOperations().execute(cqlInsertFormat, shortPath, longUrl);
            cassandraOperations.insert(new StringToStringEntity(longUrl, shortPath), insertOptions);
            cassandraOperations.insert(new StringToStringEntity(shortPath, longUrl), insertOptions);
        }
        return shortPath;
    }

    public String getShortURL(String longURL) {
        StringToStringEntity longToShortPath = stringToStringRepository.findById(longURL)
                .orElseThrow(() -> new IllegalStateException("long url not found: " + longURL));
        return URLGenerationService.shortPathToShortURL(longToShortPath.getValue());
    }

    public String getShortPath(String shortURL){
        if (shortURL.length() != shortURLLength
                || !shortURL.substring(0, hostname.length()).equals(hostname)) {
            throw new IllegalStateException("Invalid shortUrl: " + shortURL);
        }
        return shortURL.substring(hostname.length()+1);
    }

    public String getLongUrlFromShortUrl(String shortURL) {
        String shortPath = getShortPath(shortURL);
        return getLongUrlFromShortUrlPath(shortPath);
    }

    public String getLongUrlFromShortUrlPath(String shortPath){
        StringToStringEntity stringToStringEntity = stringToStringRepository.findById(shortPath)
                .orElseThrow(() -> new IllegalStateException("short url not found: " + shortPath));
        return stringToStringEntity.getValue();
    }
//
//    public void saveItem(ShortUrlPairItem shortUrlPairItem) {
//        cassandraOperations.getCqlOperations().execute(cqlInsertFormat, shortUrlPairItem.shortPath(), shortUrlPairItem.longURL());
//    }

    public List<UrlPairItem> getUrls(List<ShortUrlPathItem> shortUrlPathItems) {
        List<UrlPairItem> urlPairItems = new ArrayList<>();
        for (ShortUrlPathItem shortUrlPathItem: shortUrlPathItems){
            urlPairItems.add(
                    new UrlPairItem(
                            shortUrlPathItem.shortUrlPath(),
                            getLongUrlFromShortUrlPath(shortUrlPathItem.shortUrlPath())
                    )
            );
        }
        return urlPairItems;
    }
}
