package com.dlim2012.url;

import com.dlim2012.url.entity.URL;
import com.dlim2012.url.dto.LongURLItem;
import com.dlim2012.url.dto.ShortURLItem;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class URLController {

    private final URLService urlService;

    @GetMapping
    public List<URL> getURLs() {
        log.info("Get URLs.");
        return urlService.getURLs();
    }

    @GetMapping(path="api/v1/url/short")
    public ShortURLItem shortURL(@RequestBody LongURLItem urlRequest){
        String shortPath = urlService.shortURLPath(urlRequest.longURL());
        log.info("Get short URL: " + urlRequest.toString() + " -> " + shortPath);
        return new ShortURLItem(shortPath);
    }

    @GetMapping(path="api/v1/url/long")
    public LongURLItem longURL (@RequestBody ShortURLItem urlRequest){
        String longPath = urlService.longURL(urlRequest.shortURL());
        log.info("Get short URL: " + urlRequest.toString() + " -> " + longPath);
        return new LongURLItem(longPath);
    }

    @GetMapping(path="{shortPath}")
    public RedirectView redirect(@PathVariable("shortPath") String shortPath){
        String longPath = urlService.longURL(shortPath);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(longPath);
        return redirectView;
    }

}
