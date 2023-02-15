package com.dlim2012.shorturl.service;

import com.dlim2012.clients.dto.ShortUrlPairItem;
import com.dlim2012.clients.dto.ShortUrlPathQuery;
import com.dlim2012.clients.shorturl.dto.UrlExtensionRequest;
import com.dlim2012.clients.shorturl.dto.UrlGenerateRequest;
import com.dlim2012.clients.token.TokenClient;
import com.dlim2012.clients.token.config.TokenConfiguration;
import com.dlim2012.shorturl.entity.UrlEntity;
import com.dlim2012.shorturl.repository.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ShortUrlService {

    private final UrlRepository urlRepository;
    private final UrlGenerationService urlGenerationService;
    private final JwtDecoder jwtDecoder;

    private final Duration ttl = Duration.ofDays(366);

    private final String hostname;
    private final TokenConfiguration tokenConfiguration = new TokenConfiguration();
    private final int shortUrlLength;

    private final CassandraOperations cassandraOperations;
    private final InsertOptions insertOptions;

    @Autowired
    public ShortUrlService(
            UrlRepository urlRepository,
            TokenClient tokenClient,
            CassandraOperations cassandraOperations,
            JwtDecoder jwtDecoder,
            @Value("${hostname}") String hostname
    ) {
        this.urlRepository = urlRepository;
        this.cassandraOperations = cassandraOperations;
        this.jwtDecoder = jwtDecoder;
        this.insertOptions = InsertOptions.builder().ttl(ttl).build();
        this.hostname = hostname;

        this.urlGenerationService = new UrlGenerationService(tokenClient, hostname);
        this.shortUrlLength = hostname.length() + tokenConfiguration.getTokenLength() + 1;
    }

    public String getUserEmail(HttpServletRequest httpServletRequest){
        final String authHeader = httpServletRequest.getHeader("Authorization");
        final String jwtString = authHeader.substring(7);
        return jwtDecoder.decode(jwtString).getSubject();
    }

    public String generateShortUrlAndSave(UrlGenerateRequest request) {
        // Assume requested (long URL, queryName) does not exist in database
        // This is handled in UserService.generateShortUrl

        String shortPath = urlGenerationService.generateShortUrlPath();
        log.info("Short URL generated for {}: {}", request.longUrl(), shortPath);
        log.info("Saving URL pair: ({}, {})", shortPath, request.longUrl());

        try{
            cassandraOperations.insert(new UrlEntity(request.longUrl(), request.queryName(), shortPath, request.text()), insertOptions);
            cassandraOperations.insert(new UrlEntity(shortPath, "", request.longUrl(), request.queryName()), insertOptions);
        } catch (Exception e){
            throw new RuntimeException("Failed to insert data into Cassandra");
        }
        return shortPath;
    }


    public String queryShortUrlPath(String longUrl, String queryName){
        return urlRepository.findByKeyAndQueryName(longUrl, queryName)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("item not found: (%s, %s)", longUrl, queryName)))
                .getValue();
    }

    public String getShortUrlPathFromShortUrl(String shortUrl){
        if (shortUrl.length() != shortUrlLength
                || !shortUrl.substring(0, hostname.length()).equals(hostname)) {
            throw new IllegalStateException("Invalid shortUrl: " + shortUrl);
        }
        return shortUrl.substring(hostname.length()+1);
    }

    public String getShortUrlFromShortUrlPath(String shortUrlPath){
        return hostname + "/" + shortUrlPath;
    }

    public String queryShortUrl(String longUrl, String queryName) {
        UrlEntity longToShortPath = urlRepository.findByKeyAndQueryName(longUrl, queryName)
                .orElseThrow(() -> new IllegalStateException("long url not found: " + longUrl));
        return getShortUrlFromShortUrlPath(longToShortPath.getValue());
    }

    public String getLongUrl(String shortUrlPath, HttpServletRequest request) {
        UrlEntity urlEntity = urlRepository.findByKeyAndQueryName(shortUrlPath, "")
                .orElseThrow(() -> new IllegalStateException(
                        String.format("short url not found: %s", shortUrlPath))
                );
        if (urlEntity.getText().isEmpty() || urlEntity.getText().equals(getUserEmail(request))){
            return urlEntity.getValue();
        } else {
            throw new IllegalStateException("short url not found");
        }
    }

    public String getLongUrlFromShortUrlPath(String shortUrlPath){
        UrlEntity urlEntity = urlRepository.findByKeyAndQueryName(shortUrlPath, "")
                .orElseThrow(() -> new IllegalStateException(
                        String.format("short url not found: %s", shortUrlPath))
                );
        return urlEntity.getValue();
    }

    public List<ShortUrlPairItem> getUrls(List<ShortUrlPathQuery> shortUrlPathQueries) {
        List<ShortUrlPairItem> shortUrlPairItems = new ArrayList<>();
        for (ShortUrlPathQuery shortUrlPathQuery : shortUrlPathQueries){
            UrlEntity urlEntity = urlRepository.findByKeyAndQueryName(shortUrlPathQuery.shortUrlPath(), "")
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("short url not found: %s", shortUrlPathQuery.shortUrlPath()))
                    );
            shortUrlPairItems.add(
                    new ShortUrlPairItem(
                            shortUrlPathQuery.shortUrlPath(),
                            urlEntity.getValue(),
                            urlEntity.getText()
                    )
            );
        }
        return shortUrlPairItems;
    }

    public void extendExpiration(UrlExtensionRequest request) {
        // Assume the request urls are valid
        // This is handled in UserService.extendExpiration

        UrlEntity urlEntityByShortUrlPath = urlRepository.findByKeyAndQueryName(request.shortUrlPath(), "")
                .orElseThrow(() -> new IllegalStateException("Data not found in database: " + request.shortUrlPath()));
        UrlEntity urlEntityByLongUrl = urlRepository.findByKeyAndQueryName(request.longUrl(), request.queryName())
                .orElseThrow(() -> new IllegalStateException("Data not found in database: ("
                        + request.shortUrlPath() + ", " + request.queryName() + ")")
                );

        Duration ttl = Duration.ofDays(ChronoUnit.DAYS.between(LocalDate.now(), request.expireDate()));
        InsertOptions extensionInsertOptions = InsertOptions.builder().ttl(ttl).build();

        cassandraOperations.insert(urlEntityByShortUrlPath, extensionInsertOptions);
        cassandraOperations.insert(urlEntityByLongUrl, extensionInsertOptions);
    }
}
