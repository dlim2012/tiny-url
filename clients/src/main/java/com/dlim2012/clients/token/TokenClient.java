package com.dlim2012.clients.token;

import com.dlim2012.clients.token.dto.TokenItem;
import com.dlim2012.clients.token.dto.TokenSetting;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "token",
        url = "${clients.token.url}"
)
public interface TokenClient {
    @GetMapping("api/v1/token")
    public TokenItem getToken();

    @GetMapping("api/v1/token/settings")
    public TokenSetting getTokenSetting();
}
