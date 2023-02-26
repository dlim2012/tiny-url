package com.dlim2012.shorturl.service;

import com.datastax.oss.driver.shaded.guava.common.collect.Lists;
import com.dlim2012.clients.appuser.AppUserClient;
import com.dlim2012.clients.appuser.dto.ExpireDateRequest;
import com.dlim2012.clients.appuser.dto.ExpireDateResponse;
import com.dlim2012.clients.dto.ShortUrlPairItem;
import com.dlim2012.clients.dto.ShortUrlPathQuery;
import com.dlim2012.clients.dto.ShortUrlQueryResponse;
import com.dlim2012.clients.shorturl.dto.*;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ShortUrlService {

    private final UrlRepository urlRepository;
    private final UrlGenerationService urlGenerationService;
    private final JwtDecoder jwtDecoder;
    private final AppUserClient appUserClient;

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
            AppUserClient appUserClient,
            @Value("${hostname}") String hostname
    ) {
        this.urlRepository = urlRepository;
        this.cassandraOperations = cassandraOperations;
        this.jwtDecoder = jwtDecoder;
        this.appUserClient = appUserClient;
        this.insertOptions = InsertOptions.builder().ttl(ttl).build();
        this.hostname = hostname;

        this.urlGenerationService = new UrlGenerationService(tokenClient, hostname);
        this.shortUrlLength = hostname.length() + tokenConfiguration.getTokenLength() + 1;
    }

    public String getHostname(){
        return hostname;
    }

    public String getUserEmail(HttpServletRequest httpServletRequest){
        final String authHeader = httpServletRequest.getHeader("Authorization");
        final String jwtString = authHeader.substring(7);
        return jwtDecoder.decode(jwtString).getSubject();
    }

    public String getQueryNameByShortUrlPath(Boolean isPrivate, String userEmail){
        return isPrivate ? userEmail : "";
    }

    public String getQueryNameByLongUrl(Boolean isPrivate, String userEmail){
        return (isPrivate ? "1" : "0") + userEmail;
    }

    public boolean checkExists(String longUrl, Boolean isPrivate, String userEmail){
        String queryNameByLongUrl = getQueryNameByLongUrl(isPrivate, userEmail);
        Optional<UrlEntity> urlEntityOptional = urlRepository.findByKeyAndQueryName(longUrl, queryNameByLongUrl);
        return urlEntityOptional.isPresent();
    }

    public String generateShortUrlAndSave(UrlGenerateRequest request) {
        // Assume requested (long URL, queryName) does not exist in database
        // This is handled in UserService.generateShortUrl
        if (checkExists(request.longUrl(), request.isPrivate(), request.userEmail())){
            throw new IllegalStateException(String.format("%s with privacy %s is already registered by the user", request.longUrl(), request.isPrivate()));
        }
        String shortUrlPath = urlGenerationService.generateShortUrlPath();
        log.info("Short URL generated for {}: {}", request.longUrl(), shortUrlPath);
        _saveUrl(request.longUrl(), shortUrlPath, request.userEmail(), request.isPrivate(), request.text());
        return shortUrlPath;
    }

    public void saveUrl(UrlSaveRequest request){
        if (checkExists(request.longUrl(), request.isPrivate(), request.userEmail())){
            throw new IllegalStateException(String.format("%s with privacy %s is already registered by the user", request.longUrl(), request.isPrivate()));
        }
        _saveUrl(
                request.longUrl(),
                request.shortUrlPath(),
                request.userEmail(),
                request.isPrivate(),
                request.text()
        );
    }

    public void _saveUrl(String longUrl, String shortUrlPath, String userEmail, Boolean isPrivate, String text){
        log.info("Saving URL pair: ({}, {}) with privacy {}", shortUrlPath, longUrl, isPrivate);
        String queryNameByLongUrl = getQueryNameByLongUrl(isPrivate, userEmail);
        String queryNameByShortUrlPath = getQueryNameByShortUrlPath(isPrivate, userEmail);

        try{
            cassandraOperations.insert(
                    new UrlEntity(longUrl, queryNameByLongUrl, shortUrlPath, "", true), insertOptions
            );
            cassandraOperations.insert(
                    new UrlEntity(shortUrlPath, queryNameByShortUrlPath, longUrl, text, true), insertOptions
            );
        } catch (Exception e){
            throw new RuntimeException("Failed to insert data into Cassandra");
        }
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
        UrlEntity urlEntity;
        try {
            urlEntity = getUrlEntityByShortUrlPath(shortUrlPath, request);
        } catch (IllegalStateException e){
            return hostname;
        }
        if (!urlEntity.getActive()){
            return hostname;
        } else {
            return urlEntity.getValue();
        }
    }

    public UrlEntity getUrlEntityByShortUrlPath(String shortUrlPath, HttpServletRequest request) {
        Optional<UrlEntity> urlEntityOptional = urlRepository.findByKeyAndQueryName(shortUrlPath, "");
        if (urlEntityOptional.isPresent()){
            return urlEntityOptional.get();
        }
        String userEmail = getUserEmail(request);
        UrlEntity urlEntity = urlRepository.findByKeyAndQueryName(shortUrlPath, userEmail)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("short url with path %s not found for the user", shortUrlPath))
                );
        return urlEntity;
    }

    public ShortUrlQueryResponse getShortUrl(String longUrl, HttpServletRequest request) {
        String userEmail = getUserEmail(request);
        List<ExpireDateRequest> expireDateRequests = new ArrayList<>();
        List<UrlEntity> urlEntities = new ArrayList<>();
        for (Character ch: Lists.charactersOf("01")){
            Optional<UrlEntity> urlEntityOptional = urlRepository.findByKeyAndQueryName(
                    longUrl,  ch+ userEmail
            );
            if (urlEntityOptional.isPresent()){
                UrlEntity urlEntity = urlEntityOptional.get();
                urlEntities.add(urlEntity);
                expireDateRequests.add(new ExpireDateRequest(urlEntity.getValue()));
            }
        }
        List<ExpireDateResponse> expireDateResponses = appUserClient.getExpireDate(expireDateRequests);
        HashMap<String, LocalDate> expireDates = new HashMap<>();
        for (ExpireDateResponse expireDateResponse: expireDateResponses){
            expireDates.put(expireDateResponse.shortUrlPath(), expireDateResponse.expireDate());
        }
        ShortUrlQueryResponse shortUrlQueryResponse = new ShortUrlQueryResponse();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (UrlEntity urlEntity: urlEntities){
            LocalDate expireDate = expireDates.get(urlEntity.getValue());
            if (expireDate != null){
                shortUrlQueryResponse.set(
                        urlEntity.getQueryName().charAt(0) == '1',
                        getShortUrlFromShortUrlPath(urlEntity.getValue()),
                        urlEntity.getText(),
                        expireDate.format(formatter),
                        urlEntity.getActive()
                );
            }
        }
        return shortUrlQueryResponse;
    }

    public String getShortUrlPath(ShortUrlPathQueryRequest request) {
        Optional<UrlEntity> urlEntityOptional = urlRepository.findByKeyAndQueryName(
                request.longUrl(), (request.isPrivate() ? "1" : "0") + request.userEmail());
        if (urlEntityOptional.isPresent()){
            return urlEntityOptional.get().getValue();
        }
        return "";
    }

    public List<ShortUrlPairItem> getUrls(List<ShortUrlPathQuery> shortUrlPathQueries) {
        List<ShortUrlPairItem> shortUrlPairItems = new ArrayList<>();
        for (ShortUrlPathQuery shortUrlPathQuery : shortUrlPathQueries){
            String queryName = shortUrlPathQuery.isPrivate() ? shortUrlPathQuery.userEmail() : "";
            try {
                UrlEntity urlEntity = urlRepository
                        .findByKeyAndQueryName(shortUrlPathQuery.shortUrlPath(), queryName)
                        .orElseThrow(() -> new IllegalStateException(
                                String.format("Short URL with path %s and privacy %s not found the user",
                                        shortUrlPathQuery.shortUrlPath(), shortUrlPathQuery.isPrivate()))
                        );
                System.out.println(urlEntity.getText());
                shortUrlPairItems.add(
                        new ShortUrlPairItem(
                                shortUrlPathQuery.shortUrlPath(),
                                urlEntity.getValue(),
                                urlEntity.getText(),
                                shortUrlPathQuery.isPrivate(),
                                urlEntity.getActive(),
                                shortUrlPathQuery.expireDate()
                        )
                );
            } catch (Exception e) {
                log.info(e.toString());
            }
        }
        return shortUrlPairItems;
    }

    public UrlExtensionResponse extendExpiration(UrlExtensionRequest request) {
        // Assume the request urls are valid
        // This is handled in UserService.extendExpiration


        final String queryNameByLongUrl = getQueryNameByLongUrl(request.isPrivate(), request.userEmail());
        final String queryNameByShortUrlPath = getQueryNameByShortUrlPath(request.isPrivate(), request.userEmail());

        final UrlEntity urlEntityByLongUrl = urlRepository.findByKeyAndQueryName(
                request.longUrl(), queryNameByLongUrl
        ).orElseThrow(() -> new IllegalStateException(
                String.format("URL with path %s and privacy %s not found for the user", request.longUrl(), request.isPrivate())
        ));

        final String shortUrlPath = urlEntityByLongUrl.getValue();

        final UrlEntity urlEntityByShortUrlPath = urlRepository.findByKeyAndQueryName(
                shortUrlPath, queryNameByShortUrlPath
        ).orElseThrow(() -> new IllegalStateException(String.format("Short URL with path %s and privacy %s not found for the user",
                shortUrlPath, request.isPrivate()))
        );

        Duration ttl = Duration.ofSeconds(urlRepository.getTtl(shortUrlPath, queryNameByShortUrlPath)).plusDays(366);
        InsertOptions extensionInsertOptions = InsertOptions.builder().ttl(ttl).build();

        cassandraOperations.insert(urlEntityByShortUrlPath, extensionInsertOptions);
        cassandraOperations.insert(urlEntityByLongUrl, extensionInsertOptions);
        return new UrlExtensionResponse(shortUrlPath);
    }

    public void setIsActive(ModifyIsActiveRequest request) {
        final String queryNameByShortUrlPath = getQueryNameByShortUrlPath(request.isPrivate(), request.userEmail());
        final String queryNameByLongUrl = getQueryNameByLongUrl(request.isPrivate(), request.userEmail());

        UrlEntity urlEntityByShortUrlPath = urlRepository.findByKeyAndQueryName(
                request.shortUrlPath(), queryNameByShortUrlPath
        ).orElseThrow(() -> new IllegalStateException(String.format("Short URL with path %s and privacy %s not found the user",
                request.shortUrlPath(), request.isPrivate()))
        );

        urlRepository.updateIsActive(request.shortUrlPath(), queryNameByShortUrlPath, request.isActive());
        urlRepository.updateIsActive(urlEntityByShortUrlPath.getValue(), queryNameByLongUrl, request.isActive());
    }

    public ModifyPathResponse modifyPath(ModifyPathRequest request) {
        final String queryNameByShortUrlPath = getQueryNameByShortUrlPath(request.isPrivate(), request.userEmail());
        final String queryNameByLongUrl = getQueryNameByLongUrl(request.isPrivate(), request.userEmail());

        UrlEntity urlEntityByLongUrl = urlRepository.findByKeyAndQueryName(
                request.longUrl(), queryNameByLongUrl
        ). orElseThrow(
                () -> new IllegalStateException(
                        String.format("URL with path %s and privacy %s not found for the user", request.longUrl(), request.isPrivate()))
        );
        final String prevShortUrlPath = urlEntityByLongUrl.getValue();
        final String newShortUrlPath = request.newShortUrlPath().isEmpty() ?
                urlGenerationService.generateShortUrlPath() : request.newShortUrlPath();

        int ttl = urlRepository.getTtl(prevShortUrlPath, queryNameByShortUrlPath);
        InsertOptions modificationInsertOptions = InsertOptions.builder().ttl(ttl).build();

        UrlEntity urlEntityByShortUrlPath = urlRepository.findByKeyAndQueryName(
                prevShortUrlPath, queryNameByShortUrlPath
        ).orElseThrow(() -> new IllegalStateException(String.format("Short URL with path %s and privacy %s not found the user",
                prevShortUrlPath, request.isPrivate()))
        );

        // update when key is long URL
        urlRepository.delete(urlEntityByLongUrl);
        cassandraOperations.insert(
                new UrlEntity(request.longUrl(), queryNameByLongUrl, newShortUrlPath, "", true),
                modificationInsertOptions
        );

        // update when key is short URL
        urlRepository.delete(urlEntityByShortUrlPath);
        cassandraOperations.insert(
                new UrlEntity(newShortUrlPath, queryNameByShortUrlPath, request.longUrl(), request.text(), true),
                modificationInsertOptions
        );

        return new ModifyPathResponse(
                prevShortUrlPath,
                newShortUrlPath
        );
    }
}
