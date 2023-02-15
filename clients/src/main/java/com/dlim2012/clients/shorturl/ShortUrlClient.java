package com.dlim2012.clients.shorturl;


import com.dlim2012.clients.dto.ShortUrlPairItem;
import com.dlim2012.clients.dto.ShortUrlPathItem;
import com.dlim2012.clients.dto.ShortUrlPathQuery;
import com.dlim2012.clients.shorturl.dto.ShortUrlPathQueryRequest;
import com.dlim2012.clients.shorturl.dto.UrlExtensionRequest;
import com.dlim2012.clients.shorturl.dto.UrlGenerateRequest;
import com.dlim2012.clients.shorturl.dto.UrlSaveRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(
        name = "shorturl",
        url = "${clients.shorturl.url}"
)
public interface ShortUrlClient {

    @PostMapping(path="/shorturl/generate")
    ShortUrlPathItem generateShortPathAndSave(@RequestBody UrlGenerateRequest generateRequest);

    @RequestMapping(path="/shorturl/urls")
    List<ShortUrlPairItem> getUrls(@RequestBody List<ShortUrlPathQuery> shortUrlPathQueries);

    @PostMapping(path="/shorturl/extend")
    void extendExpiration(@RequestBody UrlExtensionRequest urlExtensionRequest);

    @PostMapping(path="/shorturl/shortpath")
    ShortUrlPathQuery getShortURLPath(@RequestBody ShortUrlPathQueryRequest shortUrlPathQueryRequest);

    @PostMapping(path="/shorturl/save")
    public void saveUrl(@RequestBody UrlSaveRequest urlSaveRequest);
}
