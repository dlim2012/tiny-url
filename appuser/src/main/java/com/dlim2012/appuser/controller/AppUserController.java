package com.dlim2012.appuser.controller;


import com.dlim2012.appuser.dto.*;
import com.dlim2012.appuser.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin
public class AppUserController {
    private final UserService userService;


    @PostMapping("/generate")
    public GenerationResponse generateShortUrl(HttpServletRequest request, @RequestBody GenerationRequest generationRequest){
        String userEmail = userService.getUserEmail(request);
        log.info(
                "Short URL generate request from user {} for {} (custom path {})",
                userEmail, generationRequest.longUrl(), generationRequest.shortUrlPath()
        );
        return userService.generateShortUrl(userEmail, generationRequest);
    }

    @PostMapping("/extend")
    public ExtensionResponse extendExpiration(HttpServletRequest request, @RequestBody ExtensionRequest extensionRequest){
        String userEmail = userService.getUserEmail(request);
        log.info(
                "Short URL extension request from user {}: {}",
                userEmail,
                extensionRequest.shortUrl()
        );
        return userService.extendExpiration(userEmail, extensionRequest);
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

    // todo: update active/inactive to Cassandra
    @PostMapping(path="/urls/disactivate")
    public void disactivateUserUrl(HttpServletRequest request, @RequestBody ModifyUrlRequest modifyUrlRequest) {
        String userEmail = userService.getUserEmail(request);
        log.info("Getting all urls of user {}", userEmail);
        userService.disActivateUserUrl(userEmail, modifyUrlRequest);
    }

    @PostMapping(path="/urls/activate")
    public void activateUserUrl(HttpServletRequest request, @RequestBody ModifyUrlRequest modifyUrlRequest) {
        String userEmail = userService.getUserEmail(request);
        log.info("Getting all urls of user {}", userEmail);
        userService.activateUserUrl(userEmail, modifyUrlRequest);
    }

    @GetMapping(path="/profile")
    public GetProfileResponse getProfile(HttpServletRequest request){
        String userEmail = userService.getUserEmail(request);
        log.info("Get profile of user {}", userEmail);
        return userService.getProfile(userEmail);

    }


}
