package com.dlim2012.appuser.service;

import com.dlim2012.appuser.dto.*;
import com.dlim2012.appuser.entity.AppUser;
import com.dlim2012.appuser.entity.UrlEntity;
import com.dlim2012.appuser.repository.AppUserRepository;
import com.dlim2012.appuser.repository.UrlRepository;
import com.dlim2012.clients.appuser.dto.ExpireDateRequest;
import com.dlim2012.clients.appuser.dto.ExpireDateResponse;
import com.dlim2012.appuser.dto.ShortUrlQueryResponse;
import com.dlim2012.clients.token.TokenClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final AppUserRepository appUserRepository;
    private final UrlRepository urlRepository;
    private final JwtDecoder jwtDecoder;

    private final String hostname;
    private final UrlGenerationService urlGenerationService;
    private final DateTimeFormatter dateTimeFormatter;


    @Autowired
    public UserService(
            AppUserRepository appUserRepository,
            UrlRepository urlRepository,
            JwtDecoder jwtDecoder,
            TokenClient tokenClient,
            @Value("${hostname}") String hostname
    ) throws InterruptedException {
        this.appUserRepository = appUserRepository;
        this.urlRepository = urlRepository;
        this.jwtDecoder = jwtDecoder;
        this.hostname = hostname;
        this.urlGenerationService = new UrlGenerationService(tokenClient, hostname);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public String getUserEmail(HttpServletRequest httpServletRequest) {
        final String authHeader = httpServletRequest.getHeader("Authorization");
        final String jwtString = authHeader.substring(7);
        Jwt jwt = jwtDecoder.decode(jwtString);
        return jwt.getSubject();
    }


    @Cacheable(cacheNames = "cache2", key = "'#app_user'")
    AppUser getAppUser(String userEmail) {
        return appUserRepository.findByEmail(userEmail).orElseThrow(
                () -> new IllegalStateException("Authorized user does not exist in repository.")
        );
    }

    @Cacheable(cacheNames = "cache2", key = "'#app_user'")
    AppUser getAppUserForUpdate(String userEmail) {
        return appUserRepository.findByEmailForUpdate(userEmail).orElseThrow(
                () -> new IllegalStateException("Authorized user does not exist in repository.")
        );
    }

    public String getShortUrlFromShortUrlPath(String shortUrlPath) {
        return hostname + '/' + shortUrlPath;
    }

    public String getShortUrlPathFromShortUrl(String shortUrl) {
        return shortUrl.substring(hostname.length() + 1);
    }

    public String getErrorMessageFromResponse(String response) {
        int i1 = response.indexOf('{');
        int i2 = response.lastIndexOf('}');
        System.out.println(response);
        JSONObject obj;
        try {
            obj = new JSONObject(response.substring(i1, i2 + 1));
            return obj.getString("message");
        } catch (Exception e) {
            throw new IllegalStateException("");
        }
    }

    public GenerationResponse generateShortUrl(
            String userEmail,
            GenerationRequest generationRequest
    ) {
        String longUrl = generationRequest.longUrl();
        if (longUrl.length() <= 7) {
            throw new IllegalStateException("Provided long URL is too short (minimum length 8 required)");
        }
        AppUser appUser = getAppUserForUpdate(userEmail);
        final int availableShortUrl = appUser.getAvailableShortUrl();
        final String shortUrlPath;

        if (!generationRequest.shortUrlPath().isEmpty()) {
            shortUrlPath = generationRequest.shortUrlPath();
            if (shortUrlPath.length() <= 7) {
                throw new IllegalStateException("Custom path should have length of at least 8");
            }
            Optional<UrlEntity> urlEntityOptional = urlRepository
                    .findByShortUrlPathForUpdate(shortUrlPath);
            if (urlEntityOptional.isPresent()) {
                UrlEntity urlEntity = urlEntityOptional.get();
                urlRepository.saveAndFlush(urlEntity);
                appUserRepository.saveAndFlush(appUser);
                if (!urlEntity.getAppUser().getId().equals(appUser.getId())){
                    throw new IllegalStateException("Custom short URLs already taken");
                }
                throw new IllegalStateException("The custom path is already taken by this account");
            }
        } else {
            Optional<UrlEntity> urlEntityOptional = urlRepository.findByUserIdAndLongUrlAndIsPrivate(
                    appUser.getId(), longUrl, generationRequest.isPrivate()
            );
            if (urlEntityOptional.isPresent()) {
                throw new IllegalStateException(
                        String.format("The %s URL is already registered by this user.",
                                generationRequest.isPrivate() ? "private" : "public")
                );
            }
            shortUrlPath = this.urlGenerationService.generateShortUrlPath();
        }

        if (availableShortUrl <= 0) {
            throw new IllegalStateException("Not enough available short URLs.");
        }
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime expiresAt = now.plusYears(1);
        UrlEntity urlEntity = UrlEntity.builder()
                .shortUrlPath(shortUrlPath)
                .longUrl(generationRequest.longUrl())
                .text(generationRequest.description())
                .createdAt(now)
                .expiresAt(expiresAt)
                .isPrivate(generationRequest.isPrivate())
                .isActive(true)
                .appUser(appUser)
                .build();
        appUser.getUrlEntities().add(urlEntity);

        urlRepository.saveAndFlush(urlEntity);
        int remainingShortUrl = availableShortUrl - 1;
        appUser.setAvailableShortUrl(remainingShortUrl);
        appUserRepository.saveAndFlush(appUser);



        return new GenerationResponse(
                true,
                remainingShortUrl, getShortUrlFromShortUrlPath(shortUrlPath),
                generationRequest.isPrivate(), true,
                expiresAt.toLocalDate());
    }


    public ExtensionResponse extendExpiration(String userEmail, ExtensionRequest request) {
        if (request.number() < 0) {
            throw new IllegalStateException("Extension request have negative number");
        }

        AppUser appUser = getAppUserForUpdate(userEmail);
        int availableShortUrl = appUser.getAvailableShortUrl();
        int remainingNumber = availableShortUrl - request.number();
        if (remainingNumber < 0) {
            throw new IllegalStateException("Not enough Short URL left");
        }

        final UrlEntity urlEntity = urlRepository.
                findByUserIdAndLongUrlAndIsPrivateForUpdate(
                        appUser.getId(), request.longUrl(), request.isPrivate()
                ).orElseThrow(() -> new IllegalStateException(
                                (request.isPrivate() ? "Private" : "Public") + " URL %s does not exist for the user"
                        )
                );

        final LocalDateTime prevExpiresAt = urlEntity.getExpiresAt();
        final LocalDateTime newExpiresAt = prevExpiresAt.plusYears(1);

        appUser.setAvailableShortUrl(remainingNumber);
        urlEntity.setExpiresAt(newExpiresAt);
        appUserRepository.saveAndFlush(appUser);
        urlRepository.saveAndFlush(urlEntity);
        return new ExtensionResponse(true,
                prevExpiresAt.format(this.dateTimeFormatter),
                newExpiresAt.format(this.dateTimeFormatter),
                remainingNumber, getShortUrlFromShortUrlPath(urlEntity.getShortUrlPath()));
    }

    public RefillResponse refill(String userEmail, RefillRequest refillRequest) {
        AppUser appUser = getAppUserForUpdate(userEmail);
        int availableShortUrl = appUser.getAvailableShortUrl();
        int remainingShortUrl = availableShortUrl + refillRequest.number();
        if (remainingShortUrl > 100000) {
            throw new IllegalStateException("Number of available URLs cannot exceed 100,000");
        }
        appUser.setAvailableShortUrl(remainingShortUrl);
        appUserRepository.saveAndFlush(appUser);
        return new RefillResponse(availableShortUrl, remainingShortUrl);
    }


    public List<GetUrlsResponse> getUrls(String userEmail, GetUrlsRequest getUrlsRequest) {
        List<GetUrlsResponse> getUrlsResponses = new ArrayList<>();
        AppUser appUser = getAppUser(userEmail);
        List<UrlEntity> urlEntities;
        if (getUrlsRequest.isActive() == 0) {
            urlEntities = urlRepository.findByUserIdAndIsActiveOrderByCreatedAt(appUser.getId(), false);
        } else if (getUrlsRequest.isActive() == 1) {
            urlEntities = urlRepository.findByUserIdAndIsActiveOrderByCreatedAt(appUser.getId(), true);
        } else {
            urlEntities = urlRepository.findByUserIdOrderByCreatedAt(appUser.getId());
        }
        // Exception: no short urls found
        if (urlEntities.isEmpty() || urlEntities.get(0) == null) {
            return getUrlsResponses;
        }

        for (int i = 0; i < urlEntities.size(); i++) {
            UrlEntity urlEntity = urlEntities.get(i);
            getUrlsResponses.add(
                    new GetUrlsResponse(
                            i + 1,
                            getShortUrlFromShortUrlPath(urlEntity.getShortUrlPath()),
                            urlEntity.getLongUrl(),
                            urlEntity.getText(),
                            urlEntity.getPrivate() ? "O" : "X",
                            urlEntity.getActive() ? "O" : "X",
                            urlEntity.getExpiresAt().toLocalDate()
                    )
            );
        }
        return getUrlsResponses;
    }

    public void setUrlIsActive(String userEmail, SetUrlIsActiveRequest request) {
        AppUser appUser = getAppUser(userEmail);
        String shortUrlPath = getShortUrlPathFromShortUrl(request.shortUrl());
        UrlEntity urlEntity = urlRepository.
                findByUserIdAndShortUrlPathAndIsPrivate(appUser.getId(), shortUrlPath, request.isPrivate()).orElseThrow(
                        () -> new IllegalStateException(
                                String.format(
                                        "User %s doesn't have a short url %s with privacy '%s'",
                                        userEmail, shortUrlPath, request.isPrivate()
                                )
                        )
                );

        urlEntity.setIsActive(request.isActive());
        urlRepository.saveAndFlush(urlEntity);
    }

    public ShortUrlModificationResponse modifyShortUrlPath(String userEmail, ShortUrlModificationRequest request) {
        final String newShortUrlPath;
        if (request.newShortUrlPath().isEmpty()) {
            newShortUrlPath = urlGenerationService.generateShortUrlPath();
        } else if (request.newShortUrlPath().length() >= 8) {
            newShortUrlPath = request.newShortUrlPath();
        } else {
            throw new IllegalStateException("New path is too short (length <= 7)");
        }


        UrlEntity urlEntity = urlRepository
                .findByUserEmailAndLongUrlAndIsPrivateForUpdate(
                        userEmail,
                        request.longUrl(),
                        request.isPrivate()
                ).orElseThrow(
                        () -> new IllegalStateException("URL not found")
                );

        urlEntity.setShortUrlPath(newShortUrlPath);
        urlRepository.saveAndFlush(urlEntity);

        return new ShortUrlModificationResponse(
                getShortUrlFromShortUrlPath(urlEntity.getShortUrlPath()),
                getShortUrlFromShortUrlPath(newShortUrlPath),
                urlEntity.getExpiresAt()
        );
    }

    public GetProfileResponse getProfile(String userEmail) {
        AppUser appUser = getAppUser(userEmail);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new GetProfileResponse(
                appUser.getFirstname(), appUser.getLastname(), appUser.getEmail(), appUser.getAvailableShortUrl(),
                appUser.getAppUserCreatedAt().format(formatter), appUser.getUrlEntities().size()
        );
    }

    public List<ExpireDateResponse> getExpireDate(List<ExpireDateRequest> requests) {
        List<ExpireDateResponse> responses = new ArrayList<>();
        for (ExpireDateRequest request : requests) {
            Optional<UrlEntity> shortUrlPathEntityOptional = urlRepository.findByShortUrlPath(request.shortUrlPath());
            shortUrlPathEntityOptional.ifPresent(
                    urlEntity -> responses.add(new ExpireDateResponse(request.shortUrlPath(), urlEntity.getExpiresAt())));
        }
        return responses;
    }

    public List<GetUrlsResponse> deleteUrl(String userEmail, DeleteUrlRequest deleteUrlRequest) {

        final String shortUrlPath = getShortUrlPathFromShortUrl(deleteUrlRequest.shortUrlToDelete());

        AppUser appUser = getAppUserForUpdate(userEmail);
        UrlEntity urlEntity = urlRepository
                .findByShortUrlPathForUpdate(shortUrlPath
                ).orElseThrow(
                        () -> new IllegalStateException(
                                String.format("Short URL with path %s does not exist.", shortUrlPath)
                        ));

        appUser.getUrlEntities().remove(urlEntity);
        urlRepository.delete(urlEntity);
        appUserRepository.saveAndFlush(appUser);

        return getUrls(userEmail, new GetUrlsRequest(deleteUrlRequest.isActiveForGetUrls()));

    }


    public ShortUrlQueryResponse getShortUrl(String longUrl, String userEmail) {
        ShortUrlQueryResponse shortUrlQueryResponse = new ShortUrlQueryResponse();
        List<UrlEntity> urlEntities = urlRepository.findByLongUrl(longUrl);
        for (UrlEntity urlEntity: urlEntities) {
            shortUrlQueryResponse.set(
                    urlEntity.getIsPrivate(),
                    getShortUrlFromShortUrlPath(urlEntity.getShortUrlPath()),
                    urlEntity.getText(),
                    urlEntity.getExpiresAt().format(this.dateTimeFormatter),
                    urlEntity.getActive()
            );
        }
        System.out.println(shortUrlQueryResponse);
        return shortUrlQueryResponse;
    }

    @Cacheable(cacheNames = "cache1", key = "'#long'")
    public UrlEntity getUrlEntityByShortUrlPath(String shortUrlPath, HttpServletRequest request) {
        UrlEntity urlEntity = urlRepository.findByShortUrlPath(shortUrlPath)
                .orElseThrow(() -> new IllegalStateException("Short URL not found"));
        if (urlEntity.getIsPrivate()){
            if (!urlEntity.getAppUser().getEmail().equals(getUserEmail(request))){
                throw new IllegalStateException("Short URL not found");
            }
        }
        return urlEntity;
    }
}
