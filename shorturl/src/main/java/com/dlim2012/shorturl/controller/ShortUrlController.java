package com.dlim2012.shorturl.controller;

import com.dlim2012.clients.dto.*;
import com.dlim2012.clients.shorturl.dto.ShortUrlPathQueryRequest;
import com.dlim2012.clients.shorturl.dto.UrlExtensionRequest;
import com.dlim2012.clients.shorturl.dto.UrlGenerateRequest;
import com.dlim2012.clients.shorturl.dto.UrlSaveRequest;
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

    @PostMapping(path="/api/v1/shorturl/short")
    public ShortUrlItem getShortUrl(
            HttpServletRequest request,
            @RequestBody LongUrlItem longUrlItem){
        log.info("Get short URL for {}", longUrlItem.longUrl());
        try {
            String shortUrl = shortUrlService.queryShortUrl(longUrlItem.longUrl(), "");
            return new ShortUrlItem(shortUrl);
        } catch (Exception e){
            String userEmail = shortUrlService.getUserEmail(request);
            String shortUrl = shortUrlService.queryShortUrl(longUrlItem.longUrl(), userEmail);
            return new ShortUrlItem(shortUrl);
        }
    }

    @PostMapping(path="/api/v1/shorturl/long")
    public LongUrlItem getLongUrl(
            HttpServletRequest request,
            @RequestBody ShortUrlItem shortUrlItem){
        log.info("Get long URL for {}", shortUrlItem.shortUrl());
        String shortUrlPath = shortUrlService.getShortUrlPathFromShortUrl(shortUrlItem.shortUrl());
        String LongUrl = shortUrlService.getLongUrl(shortUrlPath, request);
        return new LongUrlItem(LongUrl);
    }

    @GetMapping(path="/api/v1/shorturl/redirect/{shortPath}")
    public RedirectView redirect(
            HttpServletRequest request,
            @PathVariable("shortPath") String shortUrlPath){
        log.info("Redirection request {} received", shortUrlPath);
        String longUrl = shortUrlService.getLongUrl(shortUrlPath, request);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(longUrl);
        log.info("Redirection request {}: Redirecting to {}", shortUrlPath, longUrl);
        return redirectView;
    }


    @PostMapping(path="/shorturl/shortpath")
    public ShortUrlPathItem getShortURLPath(@RequestBody ShortUrlPathQueryRequest queryRequest){
        log.info("Get short URL for {}", queryRequest.longUrl());
        try {
            String shortUrlPath = shortUrlService.queryShortUrlPath(queryRequest.longUrl(), queryRequest.queryName());
            return new ShortUrlPathItem(shortUrlPath);
        } catch (IllegalStateException e){
            return new ShortUrlPathItem("");
        }
    }
    @PostMapping(path="/shorturl/generate")
    public ShortUrlPathItem generateShortPathAndSave(@RequestBody UrlGenerateRequest generateRequest){
        log.info("Generating a short URL for {} with query name {}", generateRequest.longUrl(), generateRequest.queryName());
        return new ShortUrlPathItem(shortUrlService.generateShortUrlAndSave(generateRequest));
    }

    @PostMapping(path="/shorturl/save")
    public void saveUrl(@RequestBody UrlSaveRequest urlSaveRequest){
        log.info("Saving URL {} with query name {} and short URL Path of {}",
                urlSaveRequest.longUrl(), urlSaveRequest.queryName(), urlSaveRequest.shortUrlPath());
        shortUrlService.saveUrl(
                urlSaveRequest.longUrl(), urlSaveRequest.shortUrlPath(), urlSaveRequest.queryName(), urlSaveRequest.text()
        );
    }

    @PostMapping(path="/shorturl/extend")
    public void extendExpiration(@RequestBody UrlExtensionRequest urlExtensionRequest){
        log.info("Expiration date extension request for {} to {}",
                urlExtensionRequest.shortUrlPath(), urlExtensionRequest.expireDate());
        shortUrlService.extendExpiration(urlExtensionRequest);
    }

    @RequestMapping(path="/shorturl/urls")
    public List<ShortUrlPairItem> getUrls(@RequestBody List<ShortUrlPathQuery> shortUrlPathQueries){
        log.info("Get Urls requested for {} number of short url paths", shortUrlPathQueries.size());
        return shortUrlService.getUrls(shortUrlPathQueries);
    }

}
