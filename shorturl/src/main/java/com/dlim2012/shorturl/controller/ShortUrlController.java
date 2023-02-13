package com.dlim2012.shorturl.controller;

import com.dlim2012.clients.dto.*;
import com.dlim2012.shorturl.service.ShortUrlService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortURLService;

    // todo: remove home function
    @GetMapping("/api/v1/shorturl/hello")
    public String home(Principal principal) {
        System.out.println(principal);
        return "Hello";
    }

    @PostMapping(path="/api/v1/shorturl/short")
    public ShortUrlItem getShortURL(@RequestBody LongUrlItem longURLItem){
        log.info("Get short URL for {}", longURLItem.longUrl());
        String shortURL = shortURLService.getShortURL(longURLItem.longUrl());
        return new ShortUrlItem(shortURL);
    }

    @PostMapping(path="/api/v1/shorturl/long")
    public LongUrlItem getLongURL(@RequestBody ShortUrlItem shortURLItem){
        log.info("Get long URL for {}", shortURLItem.shortUrl());
        String longURL = shortURLService.getLongUrlFromShortUrl(shortURLItem.shortUrl());
        return new LongUrlItem(longURL);
    }

    @GetMapping(path="/{shortPath}")
    public RedirectView redirect(@PathVariable("shortPath") String shortUrlPath){
        log.info("Redirection request {} received", shortUrlPath);
        String longURL = shortURLService.getLongUrlFromShortUrlPath(shortUrlPath);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(longURL);
        log.info("Redirection request {}: Redirecting to {}", shortUrlPath, longURL);
        return redirectView;
    }

    @PostMapping(path="/shorturl/generate")
    public ShortUrlPathItem generateShortPathAndSave(@RequestBody LongUrlItem longURLItem){
        log.info("Generating a short URL for {}", longURLItem.longUrl());
        String shortURL = shortURLService.generateShortURLAndSave(longURLItem.longUrl());
        return new ShortUrlPathItem(shortURL);
    }


//    @PostMapping(path="/shorturl/save/long")
//    public void saveItem(@RequestBody ShortUrlPairItem shortUrlPairItem){
//        log.info("Saving URL pair: ({}, {})", shortUrlPairItem.shortPath(), shortUrlPairItem.longURL());
//        shortURLService.saveItem(shortUrlPairItem);
//    }

    @RequestMapping(path="/shorturl/urls")
    public List<UrlPairItem> getUrls(@RequestBody List<ShortUrlPathItem> shortUrlPathItems){
        return shortURLService.getUrls(shortUrlPathItems);
    }

}
