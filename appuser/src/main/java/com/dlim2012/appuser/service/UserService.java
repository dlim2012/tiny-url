package com.dlim2012.appuser.service;

import com.dlim2012.appuser.dto.*;
import com.dlim2012.appuser.entity.AppUser;
import com.dlim2012.appuser.entity.ShortUrlPathEntity;
import com.dlim2012.appuser.repository.AppUserRepository;
import com.dlim2012.appuser.repository.ShortUrlPathRepository;
import com.dlim2012.clients.dto.ShortUrlPairItem;
import com.dlim2012.clients.dto.ShortUrlPathQuery;
import com.dlim2012.clients.shorturl.ShortUrlClient;
import com.dlim2012.clients.shorturl.dto.ShortUrlPathQueryRequest;
import com.dlim2012.clients.shorturl.dto.UrlExtensionRequest;
import com.dlim2012.clients.shorturl.dto.UrlGenerateRequest;
import com.dlim2012.clients.shorturl.dto.UrlSaveRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final ShortUrlClient shortUrlClient;
    private final AppUserRepository appUserRepository;
    private final ShortUrlPathRepository shortUrlPathRepository;
    private final JwtDecoder jwtDecoder;

    private final String hostname;

    @Autowired
    public UserService(
            ShortUrlClient shortUrlClient,
            AppUserRepository appUserRepository,
            ShortUrlPathRepository shortUrlPathRepository,
            JwtDecoder jwtDecoder,
            @Value("${hostname}") String hostname) {
        this.shortUrlClient = shortUrlClient;
        this.appUserRepository = appUserRepository;
        this.shortUrlPathRepository = shortUrlPathRepository;
        this.jwtDecoder = jwtDecoder;
        this.hostname = hostname;
    }

    public String getUserEmail(HttpServletRequest httpServletRequest){
        final String authHeader = httpServletRequest.getHeader("Authorization");
        final String jwtString = authHeader.substring(7);
        Jwt jwt = jwtDecoder.decode(jwtString);
        return jwt.getSubject();
    }

    AppUser getAppUser(String userEmail){
        return appUserRepository.findByEmail(userEmail).orElseThrow(
                () -> new IllegalStateException("Authorized user does not exist in repository.")
        );
    }

    AppUser getAppUserForUpdate(String userEmail){
        return appUserRepository.findByEmailForUpdate(userEmail).orElseThrow(
                () -> new IllegalStateException("Authorized user does not exist in repository.")
        );
    }

    public String getShortUrlFromShortUrlPath(String shortUrlPath) {
        return hostname + '/' + shortUrlPath;
    }

    public String getShortUrlPathFromShortUrl(String shortUrl) {
        return shortUrl.substring(hostname.length()+1);
    }


    public ShortUrlResponse generateShortUrl(
            String userEmail,
            GenerationRequest generationRequest
    ){
        String longUrl = generationRequest.longUrl();
        if (longUrl.length() <= 7){
            throw new IllegalStateException(
                    String.format("Provided long URL is too short (length: %d, minimum 8 required)", longUrl.length()));
        }
        AppUser appUser = getAppUserForUpdate(userEmail);
        final int availableShortUrl = appUser.getAvailableShortUrl();
        final String shortUrlPath;
        final LocalDateTime now;
        final LocalDate expireDate;
        final String queryName = generationRequest.isPrivate() ? userEmail : "";

        if (!generationRequest.shortUrlPath().isEmpty()){
            shortUrlPath = generationRequest.shortUrlPath();
            if (shortUrlPath.length() == 7){
                throw new IllegalStateException("Custom short URLs should not have length of 7");
            }
            Optional<ShortUrlPathEntity> shortUrlPathEntityOptional = shortUrlPathRepository.findByShortUrlPathForUpdate(shortUrlPath);
            if (shortUrlPathEntityOptional.isPresent()){
                ShortUrlPathEntity shortUrlPathEntity =  shortUrlPathRepository
                        .findByUserIdAndShortUrlPathAndIsPrivate(
                        appUser.getId(), shortUrlPath, generationRequest.isPrivate()
                ).orElseThrow(
                        () -> new IllegalStateException("Custom short URLs already taken")
                );
                shortUrlPathRepository.saveAndFlush(shortUrlPathEntity);
                appUserRepository.saveAndFlush(appUser);
                return new ShortUrlResponse(availableShortUrl,
                        getShortUrlFromShortUrlPath(shortUrlPath),
                        shortUrlPathEntityOptional.get().getIsPrivate(),
                        shortUrlPathEntityOptional.get().getIsActive());
            }

            if (availableShortUrl <= 0){ throw new IllegalStateException("Not enough available short URLs."); }
            now = LocalDateTime.now();
            expireDate = now.toLocalDate().plusDays(365);
            shortUrlClient.saveUrl(
                    new UrlSaveRequest(
                            shortUrlPath,
                            queryName,
                            longUrl,
                            generationRequest.description(),
                            expireDate
                    )
            );

        } else {

            final ShortUrlPathQuery shortUrlPathQuery = shortUrlClient.getShortURLPath(
                    new ShortUrlPathQueryRequest(longUrl, queryName));

            if (!shortUrlPathQuery.shortUrlPath().isEmpty()) {
                shortUrlPath = shortUrlPathQuery.shortUrlPath();

                Optional<ShortUrlPathEntity> shortUrlPathEntityOptional =
                        shortUrlPathRepository.findByUserIdAndShortUrlPathAndIsPrivate(
                                appUser.getId(), shortUrlPath, generationRequest.isPrivate());

                if (shortUrlPathEntityOptional.isPresent()) {
                    appUserRepository.saveAndFlush(appUser);
                    return new ShortUrlResponse(availableShortUrl,
                            getShortUrlFromShortUrlPath(shortUrlPath),
                            shortUrlPathEntityOptional.get().getIsPrivate(),
                            shortUrlPathEntityOptional.get().getIsActive());
                }

                if (availableShortUrl <= 0){ throw new IllegalStateException("Not enough available short URLs."); }
                now = LocalDateTime.now();
                expireDate = now.toLocalDate().plusDays(365);
                shortUrlClient.extendExpiration(
                        new UrlExtensionRequest(expireDate.plusDays(1), shortUrlPath, longUrl, queryName)
                );

            } else {

                if (availableShortUrl <= 0){ throw new IllegalStateException("Not enough available short URLs."); }
                now = LocalDateTime.now();
                expireDate = now.toLocalDate().plusDays(365);
                shortUrlPath = shortUrlClient.generateShortPathAndSave(
                        new UrlGenerateRequest(
                                queryName,
                                longUrl,
                                generationRequest.description(),
                                expireDate
                        )
                ).shortUrlPath();
            }
        }

        ShortUrlPathEntity shortUrlPathEntity = ShortUrlPathEntity.builder()
                .shortUrlPath(shortUrlPath)
                .createdAt(now)
                .expireDate(expireDate)
                .isPrivate(generationRequest.isPrivate())
                .isActive(true)
                .build();
        appUser.getShortUrlPathEntities().add(shortUrlPathEntity);

        int remainingShortUrl = availableShortUrl - 1;
        appUser.setAvailableShortUrl(remainingShortUrl);
        appUserRepository.saveAndFlush(appUser);
        return new ShortUrlResponse(remainingShortUrl, getShortUrlFromShortUrlPath(shortUrlPath),
                 generationRequest.isPrivate(), true);
    }


    public ExtensionResponse extendExpiration(String userEmail, ExtensionRequest extensionRequest) {
        if (extensionRequest.number() < 0){
            throw new IllegalStateException("Extension request have negative number");
        }

        AppUser appUser = getAppUserForUpdate(userEmail);
        int availableShortUrl = appUser.getAvailableShortUrl();
        String shortUrlPath = getShortUrlPathFromShortUrl(extensionRequest.ShortUrl());
        Optional<ShortUrlPathEntity> shortUrlPathEntityOptional = shortUrlPathRepository.
                findByUserIdAndShortUrlPathAndIsPrivate(appUser.getId(), shortUrlPath, extensionRequest.isPrivate());
        if (shortUrlPathEntityOptional.isEmpty()){
            throw new IllegalStateException("Invalid short URL");
        }
        ShortUrlPathEntity shortUrlPathEntity = shortUrlPathEntityOptional.get();
        LocalDate prevExpireDate = shortUrlPathEntity.getExpireDate();
        if (availableShortUrl < extensionRequest.number()){
            throw new IllegalStateException("Not enough number of URLs left");
        }
        int remainingNumber = availableShortUrl - extensionRequest.number();
        LocalDate newExpireDate = prevExpireDate.plusDays(365L * extensionRequest.number());
        String queryName = extensionRequest.isPrivate() ? userEmail : "";
        shortUrlClient.extendExpiration(new UrlExtensionRequest(
                newExpireDate.plusDays(1), shortUrlPath, extensionRequest.LongUrl(), queryName)
        );

        appUser.setAvailableShortUrl(remainingNumber);
        shortUrlPathEntity.setExpireDate(newExpireDate);
        appUserRepository.saveAndFlush(appUser);
        shortUrlPathRepository.saveAndFlush(shortUrlPathEntity);

        return new ExtensionResponse(true,
                prevExpireDate, newExpireDate, remainingNumber);
    }

    public RefillResponse refill(String userEmail, RefillRequest refillRequest) {
        AppUser appUser = getAppUserForUpdate(userEmail);
        int availableShortUrl = appUser.getAvailableShortUrl();
        int remainingShortUrl = availableShortUrl + refillRequest.number();
        appUser.setAvailableShortUrl(remainingShortUrl);
        appUserRepository.saveAndFlush(appUser);
        return new RefillResponse(availableShortUrl, remainingShortUrl);
    }


    public List<GetUrlsResponse> getUrls(String userEmail, GetUrlsRequest getUrlsRequest) {
        AppUser appUser = getAppUser(userEmail);
        List<ShortUrlPathEntity> shortUrlPathEntities;
        if (getUrlsRequest.isActive() == 0){
            shortUrlPathEntities = shortUrlPathRepository.findByUserIdAndIsActive(appUser.getId(), false);
        } else if (getUrlsRequest.isActive() == 1){
            shortUrlPathEntities = shortUrlPathRepository.findByUserIdAndIsActive(appUser.getId(), true);
        } else {
            shortUrlPathEntities = shortUrlPathRepository.findByUserId(appUser.getId());
        }
        List<ShortUrlPathQuery> shortUrlPathQueries = new ArrayList<>();
        for (ShortUrlPathEntity shortUrlPathEntity: shortUrlPathEntities){
            shortUrlPathQueries.add(
                    new ShortUrlPathQuery(
                            shortUrlPathEntity.getShortUrlPath(),
                            shortUrlPathEntity.getIsPrivate() ? userEmail : ""
                    )
                );
        }
        List<ShortUrlPairItem> shortUrlPairItems = shortUrlClient.getUrls(shortUrlPathQueries);
        if (shortUrlPathEntities.size() != shortUrlPairItems.size()){
            throw new IllegalStateException("Number of URLs send does not match the number of URLs received");
        }
        List<GetUrlsResponse> getUrlsRespons = new ArrayList<>();
        for (int i=0; i<shortUrlPathEntities.size(); i++){
            ShortUrlPathEntity shortUrlPathEntity = shortUrlPathEntities.get(i);
            ShortUrlPairItem shortUrlPairItem = shortUrlPairItems.get(i);
            if (!shortUrlPathEntity.getShortUrlPath().equals(shortUrlPairItem.shortPath())){
                throw new IllegalStateException("URLs key doesn't match with the query result");
            }
            getUrlsRespons.add(
                    new GetUrlsResponse(
                            shortUrlPathEntity.getShortUrlPath(),
                            shortUrlPairItem.longURL(),
                            shortUrlPairItem.text(),
                            shortUrlPathEntity.getIsPrivate(),
                            shortUrlPathEntity.getIsActive()
                    )
            );
        }
        return getUrlsRespons;


    }

    public void disActivateUserUrl(String userEmail, ModifyUrlRequest request){
        AppUser appUser = getAppUser(userEmail);
        String shortUrlPath = getShortUrlPathFromShortUrl(request.shortUrl());
        ShortUrlPathEntity shortUrlPathEntity = shortUrlPathRepository.
                findByUserIdAndShortUrlPathAndIsPrivate(appUser.getId(), shortUrlPath, request.isPrivate()).orElseThrow(
                        () -> new IllegalStateException(
                                String.format(
                                        "User %s doesn't have a short url %s with privacy '%s'",
                                        userEmail, shortUrlPath, request.isPrivate()
                                )
                        )
                );
        shortUrlPathEntity.setIsActive(false);
        shortUrlPathRepository.saveAndFlush(shortUrlPathEntity);
    }

    public void activateUserUrl(String userEmail, ModifyUrlRequest request) {
        AppUser appUser = getAppUser(userEmail);
        String shortUrlPath = getShortUrlPathFromShortUrl(request.shortUrl());
        ShortUrlPathEntity shortUrlPathEntity = shortUrlPathRepository.
                findByUserIdAndShortUrlPathAndIsPrivate(appUser.getId(), shortUrlPath, request.isPrivate()).orElseThrow(
                        () -> new IllegalStateException(
                                String.format(
                                        "User %s doesn't have a short url %s with privacy '%s'",
                                        userEmail, shortUrlPath, request.isPrivate()
                                )
                        )
                );
        shortUrlPathEntity.setIsActive(true);
        shortUrlPathRepository.saveAndFlush(shortUrlPathEntity);
    }
}
