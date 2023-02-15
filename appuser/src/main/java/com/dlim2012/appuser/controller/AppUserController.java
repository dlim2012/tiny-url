package com.dlim2012.appuser.controller;


import com.dlim2012.appuser.dto.*;
import com.dlim2012.appuser.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping
@RequiredArgsConstructor
public class AppUserController {
    private final UserService userService;

    @PostMapping("/api/v1/user/generate")
    public ShortUrlResponse generateShortUrl(HttpServletRequest request, @RequestBody GenerationRequest generationRequest){
        String userEmail = userService.getUserEmail(request);
        log.info(
                "Short URL generate request from user {}",
                userEmail
        );
        return userService.generateShortUrl(userEmail, generationRequest);
    }

    @PostMapping("/api/v1/user/extend")
    public ExtensionResponse extendExpiration(HttpServletRequest request, @RequestBody ExtensionRequest extensionRequest){
        String userEmail = userService.getUserEmail(request);
        log.info(
                "Short URL extension request from user {}: {}",
                userEmail,
                extensionRequest.ShortUrl()
        );
        return userService.extendExpiration(userEmail, extensionRequest);
    }

    @PostMapping(path="/api/v1/user/refill")
    public RefillResponse refill(HttpServletRequest request, @RequestBody RefillRequest refillRequest){
        String userEmail = userService.getUserEmail(request);
        log.info(
                "Increasing available short url for user {}",
                userEmail
        );
        return userService.refill(userEmail, refillRequest);
    }

    @RequestMapping(path="/api/v1/user/urls")
    public List<GetUrlsResponse> getUrls(HttpServletRequest request, @RequestBody GetUrlsRequest getUrlsRequest) {
        String userEmail = userService.getUserEmail(request);
        log.info("Get urls request of user {}", userEmail);
        return userService.getUrls(userEmail, getUrlsRequest);
    }

    @PostMapping(path="/api/v1/user/urls/disactivate")
    public void disactivateUserUrl(HttpServletRequest request, @RequestBody ModifyUrlRequest modifyUrlRequest) {
        String userEmail = userService.getUserEmail(request);
        log.info("Getting all urls of user {}", userEmail);
        userService.disActivateUserUrl(userEmail, modifyUrlRequest);
    }

    @PostMapping(path="/api/v1/user/urls/activate")
    public void activateUserUrl(HttpServletRequest request, @RequestBody ModifyUrlRequest modifyUrlRequest) {
        String userEmail = userService.getUserEmail(request);
        log.info("Getting all urls of user {}", userEmail);
        userService.activateUserUrl(userEmail, modifyUrlRequest);
    }


}
