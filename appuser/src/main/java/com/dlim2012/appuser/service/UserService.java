package com.dlim2012.appuser.service;

import com.dlim2012.appuser.dto.RefillRequest;
import com.dlim2012.appuser.dto.RefillResponse;
import com.dlim2012.appuser.entity.AppUser;
import com.dlim2012.appuser.entity.ShortUrlPathEntity;
import com.dlim2012.appuser.repository.AppUserRepository;
import com.dlim2012.appuser.repository.ShortUrlPathRepository;
import com.dlim2012.clients.dto.*;
import com.dlim2012.clients.shorturl.ShortUrlClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final ShortUrlClient shortUrlClient;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;
    private final ShortUrlPathRepository shortUrlPathRepository;
    private final JwtDecoder jwtDecoder;

    // todo: move to properties file
    private final String hostname;

    @Autowired
    public UserService(
            ShortUrlClient shortUrlClient,
            JwtService jwtService,
            AppUserRepository appUserRepository,
            ShortUrlPathRepository shortUrlPathRepository,
            JwtDecoder jwtDecoder,
            @Value("${hostname}") String hostname) {
        this.shortUrlClient = shortUrlClient;
        this.jwtService = jwtService;
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

    public ShortUrlResponse generateShortUrl(
            String userEmail,
            LongUrlItem longUrlItem
    ){
        if (longUrlItem.longUrl().length() <= 7){
            throw new IllegalStateException(
                    String.format("Provided long URL is too short (length: %d)", longUrlItem.longUrl().length()));
        }
        AppUser appUser = getAppUserForUpdate(userEmail);
        int availableShortUrl = appUser.getAvailableShortUrl();
        if (availableShortUrl <= 0){
            return new ShortUrlResponse(0, "");
        }
        final String shortUrlPath;
        try {
            shortUrlPath = shortUrlClient.generateShortPathAndSave(longUrlItem).shortUrlPath();
        } catch (Exception e){
            log.info(e.getMessage());
            appUser.setAvailableShortUrl(availableShortUrl);
            return new ShortUrlResponse(availableShortUrl, "");
        }
        Optional<String> shortUrlPathOptional =
                shortUrlPathRepository.findByUserIdAndShortUrlPath(appUser.getId(), shortUrlPath);
        if (shortUrlPathOptional.isPresent()){
            appUserRepository.saveAndFlush(appUser); // release lock in mysql
            return new ShortUrlResponse(availableShortUrl, getShortUrlFromShortUrlPath(shortUrlPath));
        }
        LocalDateTime now = LocalDateTime.now();
        appUser.getShortUrlPathEntities().add(
                ShortUrlPathEntity.builder()
                .shortUrlPath(shortUrlPath)
                .createdAt(now)
                .expireDay(now.toLocalDate().plusDays(365))
                .build()
        );
        appUser.setAvailableShortUrl(availableShortUrl - 1);
        appUserRepository.saveAndFlush(appUser);
        return new ShortUrlResponse(availableShortUrl - 1, getShortUrlFromShortUrlPath(shortUrlPath));
    }


    public String getShortUrlFromShortUrlPath(String shortUrlPath) {
        return hostname + '/' + shortUrlPath;
    }


    public RefillResponse refill(String userEmail, RefillRequest refillRequest) {
        AppUser appUser = getAppUserForUpdate(userEmail);
        int availableShortUrl = appUser.getAvailableShortUrl();
        int remainingShortUrl = availableShortUrl + refillRequest.number();
        appUser.setAvailableShortUrl(remainingShortUrl);
        appUserRepository.saveAndFlush(appUser);
        return new RefillResponse(availableShortUrl, remainingShortUrl);
    }


    public List<UrlPairItem> getUrls(String userEmail) {
        AppUser appUser = getAppUser(userEmail);
        Set<ShortUrlPathEntity> shortUrlPathEntities = appUser.getShortUrlPathEntities();
        return shortUrlClient.getUrls(
                shortUrlPathEntities
                        .stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList())
        );
    }

    public ShortUrlPathItem convertToDto(ShortUrlPathEntity shortUrlPathEntity){
        return new ShortUrlPathItem(shortUrlPathEntity.getShortUrlPath());
    }

}
