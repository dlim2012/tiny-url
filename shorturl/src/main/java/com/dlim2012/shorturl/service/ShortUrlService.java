package com.dlim2012.shorturl.service;

import com.dlim2012.clients.dto.*;
import com.dlim2012.clients.shorturl.dto.ShortUrlPathQueryRequest;
import com.dlim2012.clients.shorturl.dto.UrlExtensionRequest;
import com.dlim2012.clients.shorturl.dto.UrlGenerateRequest;
import com.dlim2012.clients.shorturl.dto.UrlSaveRequest;
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
import java.util.Optional;

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

        String shortUrlPath = urlGenerationService.generateShortUrlPath();
        log.info("Short URL generated for {}: {}", request.longUrl(), shortUrlPath);
        _saveUrl(request.longUrl(), shortUrlPath, request.userEmail(), request.isPrivate(), request.text());
        return shortUrlPath;
    }

    public void saveUrl(UrlSaveRequest urlSaveRequest){
        _saveUrl(
                urlSaveRequest.longUrl(),
                urlSaveRequest.shortUrlPath(),
                urlSaveRequest.userEmail(),
                urlSaveRequest.isPrivate(),
                urlSaveRequest.text()
        );
    }

    public void _saveUrl(String longUrl, String shortUrlPath, String userEmail, Boolean isPrivate, String text){
        log.info("Saving URL pair: ({}, {}) with privacy {}", shortUrlPath, longUrl, isPrivate);

        if (isPrivate){
            try{
                cassandraOperations.insert(
                        new UrlEntity(longUrl, "1" + userEmail, shortUrlPath, text), insertOptions
                );
                cassandraOperations.insert(
                        new UrlEntity(shortUrlPath, userEmail, longUrl, ""), insertOptions
                );
            } catch (Exception e){
                throw new RuntimeException("Failed to insert data into Cassandra");
            }
        } else {

            try{
                cassandraOperations.insert(
                        new UrlEntity(longUrl, "0" + userEmail, shortUrlPath, text), insertOptions
                );
                cassandraOperations.insert(
                        new UrlEntity(shortUrlPath, "", longUrl, ""), insertOptions
                );
            } catch (Exception e){
                throw new RuntimeException("Failed to insert data into Cassandra");
            }
        }

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


    public String getLongUrl(String shortUrlPath, HttpServletRequest request) {
        Optional<UrlEntity> urlEntityOptional = urlRepository.findByKeyAndQueryName(shortUrlPath, "");
        if (urlEntityOptional.isPresent()){
            return urlEntityOptional.get().getValue();
        }
        String userEmail = getUserEmail(request);
        UrlEntity urlEntity = urlRepository.findByKeyAndQueryName(shortUrlPath, userEmail)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("short url not found for %s: %s", userEmail, shortUrlPath))
                );
        return urlEntity.getValue();
    }

    public ShortUrlQueryResponse getShortUrl(String longUrl, HttpServletRequest request) {
        String userEmail = getUserEmail(request);
        Optional<UrlEntity> urlEntityOptional0 = urlRepository.findByKeyAndQueryName(
                longUrl, "0" + userEmail
        );
        Optional<UrlEntity> urlEntityOptional1 = urlRepository.findByKeyAndQueryName(
                longUrl, "1" + userEmail
        );
        return new ShortUrlQueryResponse(
                urlEntityOptional0.isPresent() ? getShortUrlFromShortUrlPath(urlEntityOptional0.get().getValue()) : "",
                urlEntityOptional0.isPresent() ? urlEntityOptional0.get().getText() : "",
                urlEntityOptional1.isPresent() ? getShortUrlFromShortUrlPath(urlEntityOptional1.get().getValue()) : "",
                urlEntityOptional1.isPresent() ? urlEntityOptional1.get().getText() : ""
        );
    }

    public String getShortUrlPath(ShortUrlPathQueryRequest request) {
        System.out.println();
        Optional<UrlEntity> urlEntityOptional = urlRepository.findByKeyAndQueryName(
                request.longUrl(), (request.isPrivate() ? "1" : "0") + request.userEmail());
        if (urlEntityOptional.isPresent()){
            return urlEntityOptional.get().getValue();
        }
        return "";
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
            String queryName = shortUrlPathQuery.isPrivate() ? shortUrlPathQuery.userEmail() : "";
            try {
                UrlEntity urlEntity = urlRepository
                        .findByKeyAndQueryName(shortUrlPathQuery.shortUrlPath(), queryName)
                        .orElseThrow(() -> new IllegalStateException(
                                String.format("short url not found: (%s, %s)",
                                        shortUrlPathQuery.shortUrlPath(), queryName))
                        );
                shortUrlPairItems.add(
                        new ShortUrlPairItem(
                                shortUrlPathQuery.shortUrlPath(),
                                urlEntity.getValue(),
                                urlEntity.getText()
                        )
                );
            } catch (Exception e) {
                log.info(e.toString());
            }
        }
        return shortUrlPairItems;
    }

    public void extendExpiration(UrlExtensionRequest request) {
        // Assume the request urls are valid
        // This is handled in UserService.extendExpiration


        final String queryNameByLongUrl = (request.isPrivate() ? "1" : "0") + request.userEmail();
        final String queryNameByShortUrl = (request.isPrivate() ? request.userEmail() : "");
        final UrlEntity urlEntityByLongUrl;
        final UrlEntity urlEntityByShortUrlPath;

        if (request.saveIfNotExist()) {
            if (request.longUrl().isEmpty()) {
                throw new IllegalStateException("long URL should be provided to save if not exist.");
            }
            urlEntityByLongUrl = new UrlEntity(
                    request.longUrl(), queryNameByLongUrl, request.shortUrlPath(), request.text());
            urlEntityByShortUrlPath = new UrlEntity(
                    request.shortUrlPath(), queryNameByShortUrl, request.longUrl(), "");
        } else {
            urlEntityByShortUrlPath = urlRepository.findByKeyAndQueryName(
                    request.shortUrlPath(), queryNameByShortUrl
            ).orElseThrow(() -> new IllegalStateException("Data not found in database: ("
                    + request.shortUrlPath() + ", " + queryNameByShortUrl + ")")
            );
            urlEntityByLongUrl = urlRepository.findByKeyAndQueryName(
                    urlEntityByShortUrlPath.getValue(), queryNameByLongUrl
            ).orElseThrow(() -> new IllegalStateException("Data not found in database: ("
                    + urlEntityByShortUrlPath.getValue() + ", " + queryNameByLongUrl + ")"));
        }
        Duration ttl = Duration.ofDays(ChronoUnit.DAYS.between(LocalDate.now(), request.expireDate()));
        InsertOptions extensionInsertOptions = InsertOptions.builder().ttl(ttl).build();

        cassandraOperations.insert(urlEntityByShortUrlPath, extensionInsertOptions);
        cassandraOperations.insert(urlEntityByLongUrl, extensionInsertOptions);
    }

}
