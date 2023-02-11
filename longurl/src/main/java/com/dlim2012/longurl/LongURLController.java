package com.dlim2012.longurl;

import com.dlim2012.clients.longurl.dto.LongURLItem;
import com.dlim2012.clients.longurl.dto.ShortURLItem;
import com.dlim2012.clients.longurl.dto.URLPairItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class LongUrlController {
    private final LongUrlService longURLService;

    @GetMapping(path="api/v1/longurl")
    public LongURLItem getLongURL(@RequestBody ShortURLItem shortURLItem){
        log.info("Get long URL for {}", shortURLItem.shortURL());
        String longURL = longURLService.getLongUrlFromShortUrl(shortURLItem.shortURL());
        return new LongURLItem(longURL);
    }

    @PostMapping(path="api/v1/longurl")
    public void saveItem(@RequestBody URLPairItem urlPairItem){
        log.info("Saving URL pair: ({}, {})", urlPairItem.shortPath(), urlPairItem.longURL());
        longURLService.saveItem(urlPairItem);
    }

    @GetMapping(path="{shortURL}")
    public RedirectView redirect(@PathVariable("shortURL") String shortUrlPath){
        log.info("Redirection request {} received", shortUrlPath);
        String longURL = longURLService.getLongUrlFromShortUrlPath(shortUrlPath);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(longURL);
        log.info("Redirection request {}: Redirecting to {}", shortUrlPath, longURL);
        return redirectView;
    }


}
