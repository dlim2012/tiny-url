package com.dlim2012.appuser.controller;


import com.dlim2012.appuser.dto.RefillRequest;
import com.dlim2012.appuser.dto.RefillResponse;
import com.dlim2012.appuser.service.UserService;
import com.dlim2012.clients.dto.LongUrlItem;


import com.dlim2012.clients.dto.ShortUrlItem;
import com.dlim2012.clients.dto.UrlPairItem;
import com.dlim2012.clients.dto.ShortUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@Slf4j
@RequestMapping
@RequiredArgsConstructor
public class AppUserController {
    private final UserService userService;

    @PostMapping("/api/v1/user/generate")
    public ShortUrlResponse generateShortUrl(HttpServletRequest request, @RequestBody LongUrlItem longUrlItem){
        String userEmail = userService.getUserEmail(request);
        log.info(
                "Short URL generate request from user {}",
                userEmail
        );
        return userService.generateShortUrl(userEmail, longUrlItem);
    }

    // todo: make extend Expiration functions
//    @PostMapping("/api/v1/user/extend/long")
//    public ShortUrlResponse extendExpiration(HttpServletRequest request, @RequestBody LongUrlItem longUrlItem){
//        String userEmail = userService.getUserEmail(request);
//        log.info(
//                "Short URL generate request from user {}",
//                userEmail
//        );
//        return userService.extendExpiration(userEmail, longUrlItem);
//    }
//
//    @PostMapping("/api/v1/user/extend/short")
//    public ShortUrlResponse extendExpiration(HttpServletRequest request, @RequestBody ShortUrlItem shortUrlItem){
//        String userEmail = userService.getUserEmail(request);
//        log.info(
//                "Short URL generate request from user {}",
//                userEmail
//        );
//        return userService.extendExpiration(userEmail, shortUrlItem);
//    }

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
    public List<UrlPairItem> getUrls(HttpServletRequest request) {
        String userEmail = userService.getUserEmail(request);
        log.info("Getting all urls of user {}", userEmail);
        return userService.getUrls(userEmail);
    }
}
