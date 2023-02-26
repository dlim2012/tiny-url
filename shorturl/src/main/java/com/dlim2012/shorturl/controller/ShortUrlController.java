package com.dlim2012.shorturl.controller;

import com.dlim2012.clients.dto.*;
import com.dlim2012.clients.shorturl.dto.*;
import com.dlim2012.shorturl.dto.RedirectionRequest;
import com.dlim2012.shorturl.entity.UrlEntity;
import com.dlim2012.shorturl.service.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    @PostMapping(path="/api/v1/shorturl/long")
    public LongUrlItem getLongUrlFromShortUrlPath(
            HttpServletRequest request,
            @RequestBody ShortUrlPathItem shortUrlPathItem){
        log.info("Get long URL of {}", shortUrlPathItem.shortUrlPath());
        UrlEntity urlEntity = shortUrlService.getUrlEntityByShortUrlPath(shortUrlPathItem.shortUrlPath(), request);
        log.info("Get long URL of {}: returning {} (active : {})", shortUrlPathItem.shortUrlPath(), urlEntity.getValue(), urlEntity.getActive());
        return new LongUrlItem(urlEntity.getValue(), urlEntity.getActive());
    }

    @GetMapping(path="/api/v1/shorturl/redirect/{shortUrlPath}")
    public RedirectView redirect(
            HttpServletRequest request,
            @PathVariable("shortUrlPath") String shortUrlPath){
        log.info("Redirection request {} received", shortUrlPath);
        RedirectView redirectView = new RedirectView();
        String longUrl = shortUrlService.getLongUrl(shortUrlPath, request);
        redirectView.setUrl(longUrl);
        log.info("Redirection request {}: Redirecting to {}", shortUrlPath, longUrl);
        return redirectView;
    }

    @PostMapping(path="/api/v1/shorturl/redirect-json")
    public RedirectView redirectJson(
            HttpServletRequest request,
            @RequestBody RedirectionRequest redirectionRequest
            ){
        String shortUrlPath = redirectionRequest.shortUrlPath();
        return redirect(request, shortUrlPath);
    }

    @PostMapping(path="/api/v1/shorturl/short")
    public ShortUrlQueryResponse getShortUrl(
            HttpServletRequest request,
            @RequestBody LongUrlItem longUrlItem
    ){
        log.info("Get short URL for {}", longUrlItem.longUrl());
        return shortUrlService.getShortUrl(longUrlItem.longUrl(), request);
    }

    @PostMapping(path="/shorturl/shortpath")
    public ShortUrlPathItem getShortURLPath(
            @RequestBody ShortUrlPathQueryRequest queryRequest
    ){
        log.info("Get short URL Path of {} with privacy {} for user {}",
                queryRequest.longUrl(), queryRequest.isPrivate(), queryRequest.userEmail());
        String shortUrlPath = shortUrlService.getShortUrlPath(queryRequest);
        return new ShortUrlPathItem(shortUrlPath);
    }

    @PostMapping(path="/shorturl/generate")
    public ShortUrlPathItem generateShortPathAndSave(@RequestBody UrlGenerateRequest generateRequest){
        log.info("Generating a short URL of {} for {} with privacy of {}",
                generateRequest.longUrl(), generateRequest.userEmail(), generateRequest.isPrivate());
        return new ShortUrlPathItem(shortUrlService.generateShortUrlAndSave(generateRequest));
    }

    @PostMapping(path="/shorturl/save")
    public void saveUrl(@RequestBody UrlSaveRequest urlSaveRequest){
        log.info("Saving URL {} with query name {} and short URL Path of {} with privacy {}",
                urlSaveRequest.longUrl(), urlSaveRequest.userEmail(), urlSaveRequest.shortUrlPath(), urlSaveRequest.isPrivate());
        shortUrlService.saveUrl(urlSaveRequest);
    }

    @PostMapping(path="/shorturl/extend")
    public UrlExtensionResponse extendExpiration(@RequestBody UrlExtensionRequest urlExtensionRequest){
        log.info("Expiration date extension request for {} of {}",
                urlExtensionRequest.longUrl(), urlExtensionRequest.userEmail());
        return shortUrlService.extendExpiration(urlExtensionRequest);
    }

    @RequestMapping(path="/shorturl/urls")
    public List<ShortUrlPairItem> getUrls(@RequestBody List<ShortUrlPathQuery> shortUrlPathQueries){
        log.info("Get Urls requested for {} number of short url paths", shortUrlPathQueries.size());
        return shortUrlService.getUrls(shortUrlPathQueries);
    }

    @PostMapping(path="/shorturl/set-is-active")
    public void setIsActive(@RequestBody ModifyIsActiveRequest modifyIsActiveRequest){
        shortUrlService.setIsActive(modifyIsActiveRequest);
    }

    @PostMapping(path="/shorturl/modify-path")
    public ModifyPathResponse modifyPath(@RequestBody ModifyPathRequest modifyPathRequest){
        return shortUrlService.modifyPath(modifyPathRequest);
    }
}
