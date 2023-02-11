package com.dlim2012.clients.longurl;

import com.dlim2012.clients.longurl.dto.URLPairItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "longurl",
        url = "${clients.longurl.url}"
)
public interface LongURLClient {

    @PostMapping(path="api/v1/longurl")
    public void saveItem(@RequestBody URLPairItem urlPairItem);
}
