package com.dlim2012.appuser.service;

import com.dlim2012.appuser.dto.*;
import com.dlim2012.appuser.entity.AppUser;
import com.dlim2012.appuser.entity.ShortUrlPathEntity;
import com.dlim2012.appuser.repository.AppUserRepository;
import com.dlim2012.appuser.repository.ShortUrlPathRepository;
import com.dlim2012.clients.appuser.dto.ExpireDateRequest;
import com.dlim2012.clients.appuser.dto.ExpireDateResponse;
import com.dlim2012.clients.dto.ShortUrlPairItem;
import com.dlim2012.clients.dto.ShortUrlPathItem;
import com.dlim2012.clients.dto.ShortUrlPathQuery;
import com.dlim2012.clients.shorturl.ShortUrlClient;
import com.dlim2012.clients.shorturl.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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
            @Value("${hostname}") String hostname
    ) {
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

    public String getErrorMessageFromResponse(String response) {
        int i1 = response.indexOf('{');
        int i2 = response.lastIndexOf('}');
        System.out.println(response);
        JSONObject obj;
        try {
            obj = new JSONObject(response.substring(i1, i2 + 1));
            return obj.getString("message");
        } catch (Exception e){
            throw new IllegalStateException("");
        }
    }

    public GenerationResponse generateShortUrl (
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
                throw new IllegalStateException("The short URL is already used by this account");
            }

            if (availableShortUrl <= 0){ throw new IllegalStateException("Not enough available short URLs."); }
            now = LocalDateTime.now();
            expireDate = now.toLocalDate().plusDays(365);
            try {
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
            } catch (Exception e){
                throw new IllegalStateException(getErrorMessageFromResponse(e.getMessage()));
            }

        } else {
            final ShortUrlPathItem shortUrlPathItem;
            try {
                shortUrlPathItem = shortUrlClient.getShortURLPath(
                        new ShortUrlPathQueryRequest(longUrl, userEmail, generationRequest.isPrivate())
                );
            } catch (Exception e){
                throw new IllegalStateException(getErrorMessageFromResponse(e.getMessage()));
            }

            if (!shortUrlPathItem.shortUrlPath().isEmpty()) {
                final String shortUrlPath1 = shortUrlPathItem.shortUrlPath();

                Optional<ShortUrlPathEntity> shortUrlPathEntityOptional =
                        shortUrlPathRepository.findByUserIdAndShortUrlPathAndIsPrivate(
                                appUser.getId(), shortUrlPath1, generationRequest.isPrivate());


                if (shortUrlPathEntityOptional.isPresent()) {
                    appUserRepository.saveAndFlush(appUser);
                    throw new IllegalStateException("The URL is already registered by this account");
                }

                // A URL still exists in Cassandra but not in MySQL: make a new one
                if (availableShortUrl <= 0){ throw new IllegalStateException("Not enough available short URLs."); }
                now = LocalDateTime.now();
                expireDate = now.toLocalDate().plusDays(365);
                try{
                    shortUrlPath = shortUrlClient.generateShortPathAndSave(
                            new UrlGenerateRequest(
                                    userEmail,
                                    generationRequest.isPrivate(),
                                    longUrl,
                                    generationRequest.description(),
                                    expireDate
                            )
                    ).shortUrlPath();
                } catch (Exception e){
                    throw new IllegalStateException(getErrorMessageFromResponse(e.getMessage()));
                }

            } else {

                if (availableShortUrl <= 0){ throw new IllegalStateException("Not enough available short URLs."); }
                now = LocalDateTime.now();
                expireDate = now.toLocalDate().plusDays(365);
                try {
                    shortUrlPath = shortUrlClient.generateShortPathAndSave(
                            new UrlGenerateRequest(
                                    userEmail,
                                    generationRequest.isPrivate(),
                                    longUrl,
                                    generationRequest.description(),
                                    expireDate
                            )
                    ).shortUrlPath();
                } catch (Exception e){
                    throw new IllegalStateException(getErrorMessageFromResponse(e.getMessage()));
                }

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


    public ExtensionResponse extendExpiration(String userEmail, ExtensionRequest request) {
        if (request.number() < 0){
            throw new IllegalStateException("Extension request have negative number");
        }

        AppUser appUser = getAppUserForUpdate(userEmail);
        int availableShortUrl = appUser.getAvailableShortUrl();
        int remainingNumber = availableShortUrl - request.number();
        if (remainingNumber < 0){
            throw new IllegalStateException("Not enough Short URL left");
        }

        final String shortUrlPath;
        try {
            shortUrlPath= shortUrlClient.extendExpiration(
                    new UrlExtensionRequest(
                            request.longUrl(),
                            userEmail,
                            request.isPrivate()
                    )
            ).shortUrlPath();
        } catch (Exception e){
            throw new IllegalStateException(getErrorMessageFromResponse(e.getMessage()));
        }

        final ShortUrlPathEntity shortUrlPathEntity = shortUrlPathRepository.
                findByUserIdAndShortUrlPathAndIsPrivate(appUser.getId(), shortUrlPath, request.isPrivate()).orElseThrow(
                        () -> new IllegalStateException(
                                String.format(
                                        "User %s doesn't have a short url %s with privacy '%s'",
                                        userEmail, shortUrlPath, request.isPrivate()
                                )
                        )
                );

        final LocalDate prevExpireDate = shortUrlPathEntity.getExpireDate();
        final LocalDate newExpireDate = prevExpireDate.plusYears(1);

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
        if (remainingShortUrl > 100000){
            throw new IllegalStateException("Number of available URLs cannot exceed 100,000");
        }
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
        for (ShortUrlPathEntity shortUrlPathEntity: shortUrlPathEntities) {
            shortUrlPathQueries.add(
                    new ShortUrlPathQuery(
                            shortUrlPathEntity.getShortUrlPath(),
                            userEmail,
                            shortUrlPathEntity.getIsPrivate(),
                            shortUrlPathEntity.getExpireDate()
                    )
            );
        }
        List<ShortUrlPairItem> shortUrlPairItems;
        try {
            shortUrlPairItems= shortUrlClient.getUrls(shortUrlPathQueries);
        } catch (Exception e){
            throw new IllegalStateException(getErrorMessageFromResponse(e.getMessage()));
        }

        if (shortUrlPathEntities.size() != shortUrlPairItems.size()){
            log.info("Consistency Warning: Number of URLs send does not match the number of URLs received");
        }

        for (int i=0; i<shortUrlPairItems.size(); i++){
            ShortUrlPairItem shortUrlPairItem = shortUrlPairItems.get(i);
            getUrlsResponses.add(
                    new GetUrlsResponse(
                            i+1,
                            getShortUrlFromShortUrlPath(shortUrlPairItem.shortUrlPath()),
                            shortUrlPairItem.longURL(),
                            shortUrlPairItem.text(),
                            shortUrlPairItem.isPrivate() ? "O" : "X",
                            shortUrlPairItem.isActive() ? "O" : "X",
                            shortUrlPairItem.expireDate()
                    )
            );
        }
        return getUrlsResponses;
    }

    public void setUrlIsActive(String userEmail, setUrlIsActiveRequest request) {
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

        try {
            shortUrlClient.setIsActive(
                    new ModifyIsActiveRequest(
                            shortUrlPath, userEmail, request.isPrivate(), request.isActive()
                    )
            );
        } catch (Exception e){
            throw new IllegalStateException(getErrorMessageFromResponse(e.getMessage()));
        }

        shortUrlPathEntity.setIsActive(request.isActive());
        shortUrlPathRepository.saveAndFlush(shortUrlPathEntity);
    }

    public ShortUrlModificationResponse modifyShortUrlPath(String userEmail, ShortUrlModificationRequest request) {
        if (!request.newShortUrlPath().isEmpty() && request.newShortUrlPath().length() <= 7){
            throw new IllegalStateException("New path is too short (length <= 7)");
        }
        AppUser appUser = getAppUserForUpdate(userEmail);
        ModifyPathResponse modifyPathResponse;
        try {
            modifyPathResponse = shortUrlClient.modifyPath(
                new ModifyPathRequest(
                        request.longUrl(),
                        request.newShortUrlPath(),
                        userEmail,
                        request.isPrivate(),
                        request.newDescription()
                    )
            );
        } catch (Exception e){
            throw new IllegalStateException(getErrorMessageFromResponse(e.getMessage()));
        }

        ShortUrlPathEntity shortUrlPathEntity = shortUrlPathRepository
                .findByUserIdAndShortUrlPathAndIsPrivate(appUser.getId(), modifyPathResponse.prevShortUrlPath(), request.isPrivate())
                .orElseThrow(() -> new IllegalStateException("Entity not found."));

        appUser.getShortUrlPathEntities().remove(shortUrlPathEntity);
        appUser.getShortUrlPathEntities().add(
                new ShortUrlPathEntity(modifyPathResponse.newShortUrlPath(), shortUrlPathEntity.getCreatedAt(),
                        shortUrlPathEntity.getExpireDate(), shortUrlPathEntity.getIsPrivate(),
                        shortUrlPathEntity.getIsActive())
        );
        appUserRepository.saveAndFlush(appUser);

        return new ShortUrlModificationResponse(
                request.longUrl(),
                getShortUrlFromShortUrlPath(modifyPathResponse.prevShortUrlPath()),
                getShortUrlFromShortUrlPath(modifyPathResponse.newShortUrlPath()),
                request.isPrivate(),
                shortUrlPathEntity.getExpireDate()
        );
    }

    public GetProfileResponse getProfile(String userEmail) {
        AppUser appUser = getAppUser(userEmail);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new GetProfileResponse(
                appUser.getFirstname(), appUser.getLastname(), appUser.getEmail(), appUser.getAvailableShortUrl(),
                appUser.getAppUserCreatedAt().format(formatter), appUser.getShortUrlPathEntities().size()
        );
    }

    public List<ExpireDateResponse> getExpireDate(List<ExpireDateRequest> requests) {
        List<ExpireDateResponse> responses = new ArrayList<>();
        for (ExpireDateRequest request: requests){
            Optional<ShortUrlPathEntity> shortUrlPathEntityOptional = shortUrlPathRepository.findByShortUrlPath(request.shortUrlPath());
            shortUrlPathEntityOptional.ifPresent(shortUrlPathEntity -> responses.add(new ExpireDateResponse(request.shortUrlPath(), shortUrlPathEntity.getExpireDate())));
        }
        return responses;
    }
}
