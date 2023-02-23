package com.dlim2012.appuser.service;

import com.dlim2012.appuser.dto.*;
import com.dlim2012.appuser.entity.AppUser;
import com.dlim2012.appuser.entity.ShortUrlPathEntity;
import com.dlim2012.appuser.repository.AppUserRepository;
import com.dlim2012.appuser.repository.ShortUrlPathRepository;
import com.dlim2012.clients.dto.ShortUrlPairItem;
import com.dlim2012.clients.dto.ShortUrlPathItem;
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
import java.time.format.DateTimeFormatter;
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


    public GenerationResponse generateShortUrl(
            String userEmail,
            GenerationRequest generationRequest
    ){
        System.out.println(generationRequest.shortUrlPath());
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

        if (!generationRequest.shortUrlPath().isEmpty()){
            shortUrlPath = generationRequest.shortUrlPath();
            if (shortUrlPath.length() == 7){
                throw new IllegalStateException("Custom short URLs should not have length of 7");
            }
            Optional<ShortUrlPathEntity> shortUrlPathEntityOptional = shortUrlPathRepository
                    .findByShortUrlPathForUpdate(shortUrlPath);
            if (shortUrlPathEntityOptional.isPresent()){
                ShortUrlPathEntity shortUrlPathEntity =  shortUrlPathRepository
                        .findByUserIdAndShortUrlPathAndIsPrivate(
                        appUser.getId(), shortUrlPath, generationRequest.isPrivate()
                ).orElseThrow(
                        () -> new IllegalStateException("Custom short URLs already taken")
                );
                shortUrlPathRepository.saveAndFlush(shortUrlPathEntity);
                appUserRepository.saveAndFlush(appUser);
                return new GenerationResponse(
                        false,
                        availableShortUrl,
                        getShortUrlFromShortUrlPath(shortUrlPath),
                        shortUrlPathEntityOptional.get().getIsPrivate(),
                        shortUrlPathEntityOptional.get().getIsActive(),
                        shortUrlPathEntityOptional.get().getExpireDate()
                );
            }

            if (availableShortUrl <= 0){ throw new IllegalStateException("Not enough available short URLs."); }
            now = LocalDateTime.now();
            expireDate = now.toLocalDate().plusDays(365);
            shortUrlClient.saveUrl(
                    new UrlSaveRequest(
                            shortUrlPath,
                            userEmail,
                            generationRequest.isPrivate(),
                            longUrl,
                            generationRequest.description(),
                            expireDate
                    )
            );

        } else {

            final ShortUrlPathItem shortUrlPathItem = shortUrlClient.getShortURLPath(
                    new ShortUrlPathQueryRequest(longUrl, userEmail, generationRequest.isPrivate())
            );

            if (!shortUrlPathItem.shortUrlPath().isEmpty()) {
                final String shortUrlPath1 = shortUrlPathItem.shortUrlPath();

                Optional<ShortUrlPathEntity> shortUrlPathEntityOptional =
                        shortUrlPathRepository.findByUserIdAndShortUrlPathAndIsPrivate(
                                appUser.getId(), shortUrlPath1, generationRequest.isPrivate());


                if (shortUrlPathEntityOptional.isPresent()) {
                    appUserRepository.saveAndFlush(appUser);
                    return new GenerationResponse(
                            false,
                            availableShortUrl,
                            getShortUrlFromShortUrlPath(shortUrlPath1),
                            shortUrlPathEntityOptional.get().getIsPrivate(),
                            shortUrlPathEntityOptional.get().getIsActive(),
                            shortUrlPathEntityOptional.get().getExpireDate());
                }

                // A URL still exists in Cassandra but not in MySQL: make a new one
                if (availableShortUrl <= 0){ throw new IllegalStateException("Not enough available short URLs."); }
                now = LocalDateTime.now();
                expireDate = now.toLocalDate().plusDays(365);
                shortUrlPath = shortUrlClient.generateShortPathAndSave(
                        new UrlGenerateRequest(
                                userEmail,
                                generationRequest.isPrivate(),
                                longUrl,
                                generationRequest.description(),
                                expireDate
                        )
                ).shortUrlPath();

            } else {

                if (availableShortUrl <= 0){ throw new IllegalStateException("Not enough available short URLs."); }
                now = LocalDateTime.now();
                expireDate = now.toLocalDate().plusDays(365);
                shortUrlPath = shortUrlClient.generateShortPathAndSave(
                        new UrlGenerateRequest(
                                userEmail,
                                generationRequest.isPrivate(),
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
        return new GenerationResponse(
                true,
                remainingShortUrl, getShortUrlFromShortUrlPath(shortUrlPath),
                 generationRequest.isPrivate(), true,
                expireDate);
    }


    public ExtensionResponse extendExpiration(String userEmail, ExtensionRequest extensionRequest) {
        if (extensionRequest.number() < 0){
            throw new IllegalStateException("Extension request have negative number");
        }

        AppUser appUser = getAppUserForUpdate(userEmail);
        int availableShortUrl = appUser.getAvailableShortUrl();
        String shortUrlPath = getShortUrlPathFromShortUrl(extensionRequest.shortUrl());
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
        LocalDate newExpireDate = prevExpireDate.plusYears(extensionRequest.number());
        shortUrlClient.extendExpiration(new UrlExtensionRequest(
                newExpireDate.plusDays(1), shortUrlPath, "", userEmail,
                "", extensionRequest.isPrivate(), false)
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
        List<GetUrlsResponse> getUrlsResponses = new ArrayList<>();
        AppUser appUser = getAppUser(userEmail);
        List<ShortUrlPathEntity> shortUrlPathEntities;
        if (getUrlsRequest.isActive() == 0){
            shortUrlPathEntities = shortUrlPathRepository.findByUserIdAndIsActiveOrderByCreatedAt(appUser.getId(), false);
        } else if (getUrlsRequest.isActive() == 1){
            shortUrlPathEntities = shortUrlPathRepository.findByUserIdAndIsActiveOrderByCreatedAt(appUser.getId(), true);
        } else {
            shortUrlPathEntities = shortUrlPathRepository.findByUserIdOrderByCreatedAt(appUser.getId());
        }
        // Exception: no short urls found
        if (shortUrlPathEntities.get(0) == null){
            return getUrlsResponses;
        }

        List<ShortUrlPathQuery> shortUrlPathQueries = new ArrayList<>();
        for (ShortUrlPathEntity shortUrlPathEntity: shortUrlPathEntities){
            shortUrlPathQueries.add(
                    new ShortUrlPathQuery(
                            shortUrlPathEntity.getShortUrlPath(),
                            userEmail,
                            shortUrlPathEntity.getIsPrivate()
                    )
                );
        }
        List<ShortUrlPairItem> shortUrlPairItems = shortUrlClient.getUrls(shortUrlPathQueries);
        if (shortUrlPathEntities.size() != shortUrlPairItems.size()){
            throw new IllegalStateException("Number of URLs send does not match the number of URLs received");
        }
        for (int i=0; i<shortUrlPathEntities.size(); i++){
            ShortUrlPathEntity shortUrlPathEntity = shortUrlPathEntities.get(i);
            ShortUrlPairItem shortUrlPairItem = shortUrlPairItems.get(i);
            if (!shortUrlPathEntity.getShortUrlPath().equals(shortUrlPairItem.shortPath())){
                throw new IllegalStateException("URLs key doesn't match with the query result");
            }
            getUrlsResponses.add(
                    new GetUrlsResponse(
                            getShortUrlFromShortUrlPath(shortUrlPathEntity.getShortUrlPath()),
                            shortUrlPairItem.longURL(),
                            shortUrlPairItem.text(),
                            shortUrlPathEntity.getIsPrivate() ? "O" : "X",
                            shortUrlPathEntity.getIsActive() ? "O" : "X",
                            shortUrlPathEntity.getExpireDate()
                    )
            );
        }
        return getUrlsResponses;


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

    public GetProfileResponse getProfile(String userEmail) {
        AppUser appUser = getAppUser(userEmail);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new GetProfileResponse(
                appUser.getFirstname(), appUser.getLastname(), appUser.getEmail(), appUser.getAvailableShortUrl(),
                appUser.getAppUserCreatedAt().format(formatter)
        );
    }
}
