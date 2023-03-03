package com.dlim2012.clients.shorturl;


import com.dlim2012.clients.dto.ShortUrlPairItem;
import com.dlim2012.clients.dto.ShortUrlPathItem;
import com.dlim2012.clients.dto.ShortUrlPathQuery;
import com.dlim2012.clients.shorturl.dto.*;
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
    UrlExtensionResponse extendExpiration(@RequestBody UrlExtensionRequest urlExtensionRequest);

    @PostMapping(path="/shorturl/shortpath")
    ShortUrlPathItem getShortURLPath(@RequestBody ShortUrlPathQueryRequest shortUrlPathQueryRequest);

    @PostMapping(path="/shorturl/save")
    public void saveUrl(@RequestBody UrlSaveRequest urlSaveRequest);

    @PostMapping(path="/shorturl/set-is-active")
    public void setIsActive(@RequestBody ModifyIsActiveRequest modifyIsActiveRequest);

    @PostMapping(path="/shorturl/modify-path")
    public ModifyPathResponse modifyPath(@RequestBody ModifyPathRequest modifyPathRequest);

    @PostMapping(path="/shorturl/delete")
    public void deleteUrl(@RequestBody UrlDeleteRequest urlDeleteRequest);
}
