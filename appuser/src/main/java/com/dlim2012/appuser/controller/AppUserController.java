package com.dlim2012.appuser.controller;


import com.dlim2012.appuser.dto.*;
import com.dlim2012.appuser.service.UserService;
import com.dlim2012.clients.appuser.dto.ExpireDateRequest;
import com.dlim2012.clients.appuser.dto.ExpireDateResponse;
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

    @PostMapping("/urls/generate")
    public GenerationResponse generateShortUrl(HttpServletRequest request, @RequestBody GenerationRequest generationRequest){
        String userEmail = userService.getUserEmail(request);
        log.info(
                "Short URL generate request from user {} for {} (custom path {})",
                userEmail, generationRequest.longUrl(), generationRequest.shortUrlPath()
        );
        return userService.generateShortUrl(userEmail, generationRequest);
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

    @PutMapping(path="/urls/set-is-active")
    public void setUrlIsActive(HttpServletRequest request, @RequestBody setUrlIsActiveRequest setUrlIsActiveRequest) {
        String userEmail = userService.getUserEmail(request);
        log.info("Modifying isActive for short URL {} with privacy {} of {}",
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

    @GetMapping(path="/profile")
    GetProfileResponse getProfile(HttpServletRequest request){
        String userEmail = userService.getUserEmail(request);
        log.info("Get profile of user {}", userEmail);
        return userService.getProfile(userEmail);
    }

    @PostMapping(path="/expire-date")
    List<ExpireDateResponse> getExpireDate(@RequestBody List<ExpireDateRequest> expireDateRequests){
        log.info("Expire Date requested");
        return userService.getExpireDate(expireDateRequests);
    }
}
