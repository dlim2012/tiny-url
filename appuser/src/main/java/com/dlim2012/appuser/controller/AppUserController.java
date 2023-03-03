package com.dlim2012.appuser.controller;


import com.dlim2012.appuser.dto.*;
import com.dlim2012.appuser.entity.UrlEntity;
import com.dlim2012.appuser.service.UserService;
import com.dlim2012.clients.appuser.dto.ExpireDateRequest;
import com.dlim2012.clients.appuser.dto.ExpireDateResponse;
import com.dlim2012.clients.dto.LongUrlItem;
import com.dlim2012.clients.dto.ShortUrlPathItem;
import com.dlim2012.appuser.dto.ShortUrlQueryResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin
public class AppUserController {
    private final UserService userService;

    @GetMapping(path="/profile")
    GetProfileResponse getProfile(HttpServletRequest request){
        String userEmail = userService.getUserEmail(request);
        log.info("Get profile of user {}", userEmail);
        return userService.getProfile(userEmail);
    }

    @PostMapping(path="/refill")
    public RefillResponse refill(HttpServletRequest request, @RequestBody RefillRequest refillRequest){
        String userEmail = userService.getUserEmail(request);
        log.info(
                "Increasing available short url for user {}",
                userEmail
        );
        return userService.refill(userEmail, refillRequest);
    }

    @RequestMapping(path="/urls")
    public List<GetUrlsResponse> getUrls(HttpServletRequest request, @RequestBody GetUrlsRequest getUrlsRequest) {
        String userEmail = userService.getUserEmail(request);
        log.info("Get urls request of user {}", userEmail);
        return userService.getUrls(userEmail, getUrlsRequest);
    }

    @PostMapping("/urls/generate")
    public GenerationResponse generateShortUrl(HttpServletRequest request, @RequestBody GenerationRequest generationRequest){
        String userEmail = userService.getUserEmail(request);
        log.info(
                "Short URL generate request from user {} for {} (custom path {})",
                userEmail, generationRequest.longUrl(), generationRequest.shortUrlPath()
        );
        return userService.generateShortUrl(userEmail, generationRequest);
    }

    @PutMapping(path="/urls/set-is-active")
    public void setUrlIsActive(HttpServletRequest request, @RequestBody SetUrlIsActiveRequest setUrlIsActiveRequest) {
        String userEmail = userService.getUserEmail(request);
        log.info("Modifying isActiveForGetUrls for short URL {} with privacy {} of {}",
                setUrlIsActiveRequest.shortUrl(), setUrlIsActiveRequest.isPrivate(), userEmail);
        userService.setUrlIsActive(userEmail, setUrlIsActiveRequest);
    }

    @PostMapping(path="/urls/modify")
    public ShortUrlModificationResponse modifyShortUrlPath(HttpServletRequest request, @RequestBody ShortUrlModificationRequest modificationRequest){
        String userEmail = userService.getUserEmail(request);
        log.info("Modifying short URL path for {} with privacy {} to be {} with newDescription {}",
                modificationRequest.longUrl(), modificationRequest.isPrivate(),
                modificationRequest.newShortUrlPath(), modificationRequest.newDescription());
        return userService.modifyShortUrlPath(userEmail, modificationRequest);
    }

    @PostMapping("/urls/extend")
    public ExtensionResponse extendExpiration(HttpServletRequest request, @RequestBody ExtensionRequest extensionRequest){
        String userEmail = userService.getUserEmail(request);
        log.info(
                "Short URL extension request from user {}: {}",
                userEmail,
                extensionRequest.longUrl()
        );
        return userService.extendExpiration(userEmail, extensionRequest);
    }

    @PostMapping(path="/urls/delete")
    public List<GetUrlsResponse> deleteUrl(HttpServletRequest request, @RequestBody DeleteUrlRequest deleteUrlRequest){
        String userEmail = userService.getUserEmail(request);
        log.info("Delete Url {} of user {} requested", deleteUrlRequest, userEmail);
        return userService.deleteUrl(userEmail, deleteUrlRequest);
    }

    @PostMapping(path="/urls/short")
    public ShortUrlQueryResponse getShortUrl(
            HttpServletRequest request,
            @RequestBody FindShortUrlRequest findShortUrlRequest
    ){
        log.info("Get short URL for {}", findShortUrlRequest.longUrl());
        String userEmail = userService.getUserEmail(request);
        return userService.getShortUrl(findShortUrlRequest.longUrl(), userEmail);
    }

    @PostMapping(path="/urls/long")
    public LongUrlItem getLongUrlFromShortUrlPath(
            HttpServletRequest request,
            @RequestBody ShortUrlPathItem shortUrlPathItem
    ){
        log.info("Get long URL of {}", shortUrlPathItem.shortUrlPath());
        UrlEntity urlEntity = userService.getUrlEntityByShortUrlPath(shortUrlPathItem.shortUrlPath(), request);
        System.out.println(urlEntity);
        log.info("Get long URL of {}: returning {} (active : {})", shortUrlPathItem.shortUrlPath(), urlEntity.getLongUrl(), urlEntity.getActive());
        return new LongUrlItem(urlEntity.getLongUrl(), urlEntity.getActive());
    }

    @GetMapping(path="/redirect/{shortUrlPath}")
    public RedirectView redirect(
            HttpServletRequest request,
            @PathVariable("shortUrlPath") String shortUrlPath){
        log.info("Redirection request {} received", shortUrlPath);
        RedirectView redirectView = new RedirectView();
        UrlEntity urlEntity = userService.getUrlEntityByShortUrlPath(shortUrlPath, request);
        redirectView.setUrl(urlEntity.getLongUrl());
        log.info("Redirection request {}: Redirecting to {}", shortUrlPath, urlEntity.getLongUrl());
        return redirectView;
    }

    @PostMapping(path="/redirect-json")
    public RedirectView redirectJson(
            HttpServletRequest request,
            @RequestBody RedirectionRequest redirectionRequest
    ){
        String shortUrlPath = redirectionRequest.shortUrlPath();
        return redirect(request, shortUrlPath);
    }

    @PostMapping(path="/expire-date")
    List<ExpireDateResponse> getExpireDate(@RequestBody List<ExpireDateRequest> expireDateRequests){
        log.info("Expire Date requested");
        return userService.getExpireDate(expireDateRequests);
    }

}
