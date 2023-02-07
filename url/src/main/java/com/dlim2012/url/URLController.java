package com.dlim2012.url;

import com.dlim2012.url.entity.URL;
import com.dlim2012.url.dto.LongURLItem;
import com.dlim2012.url.dto.ShortURLItem;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/url")
@AllArgsConstructor
public class URLController {

    private final URLService urlService;

    @GetMapping
    public List<URL> getURLs() {
        return urlService.getURLs();
    }

    @GetMapping(path="short")
    public ShortURLItem shortURL(@RequestBody LongURLItem urlRequest){
        return urlService.shortURL(urlRequest);
    }

    @GetMapping(path="long")
    public LongURLItem longURL (@RequestBody ShortURLItem urlRequest){
        return urlService.longURL(urlRequest);
    }

    //TODO: fetch page
    

}
