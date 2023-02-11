package com.dlim2012.shorturl;

import com.dlim2012.clients.longurl.dto.LongURLItem;
import com.dlim2012.clients.longurl.dto.ShortURLItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortURLService;

    @PostMapping(path="api/v1/short")
    public ShortURLItem generateShortPathAndSave(@RequestBody LongURLItem longURLItem){
        log.info("Generating new token for {}", longURLItem.longURL());
        String shortURL = shortURLService.generateShortURLAndSave(longURLItem.longURL());
        return new ShortURLItem(shortURL);
    }

    @GetMapping(path="api/v1/short")
    public ShortURLItem getShortURL(@RequestBody LongURLItem longURLItem){
        log.info("Get short URL for {}", longURLItem.longURL());
        String shortURL = shortURLService.getShortURL(longURLItem.longURL());
        return new ShortURLItem(shortURL);
    }
}
