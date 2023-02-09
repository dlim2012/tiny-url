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
public class LongURLController {
    private final LongURLService longURLService;

    @GetMapping(path="api/v1/longurl")
    public LongURLItem getLongURL(@RequestBody ShortURLItem shortURLItem){
        String longURL = longURLService.getLongURL(shortURLItem.shortURL());
        return new LongURLItem(longURL);
    }

    @PostMapping(path="api/v1/longurl")
    public void saveItem(@RequestBody URLPairItem urlPairItem){
        longURLService.saveItem(urlPairItem);
    }

    @GetMapping(path="{shortURL}")
    public RedirectView redirect(@PathVariable("shortURL") String shortURL){
        String longURL = longURLService.getLongURL(shortURL);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(longURL);
        return redirectView;
    }


}
