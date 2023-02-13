package com.dlim2012.clients.shorturl;


import com.dlim2012.clients.dto.LongUrlItem;
import com.dlim2012.clients.dto.ShortUrlPairItem;
import com.dlim2012.clients.dto.ShortUrlPathItem;
import com.dlim2012.clients.dto.UrlPairItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "shorturl",
        url = "${clients.shorturl.url}"
)
public interface ShortUrlClient {

    @PostMapping(path="/shorturl/generate")
    public ShortUrlPathItem generateShortPathAndSave(@RequestBody LongUrlItem longURLItem);

//    @PostMapping(path="/shorturl/save/long")
//    public void saveItem(@RequestBody ShortUrlPairItem shortUrlPairItem);

    @RequestMapping(path="/shorturl/urls")
    public List<UrlPairItem> getUrls(@RequestBody List<ShortUrlPathItem> shortUrlPathItems);

}
