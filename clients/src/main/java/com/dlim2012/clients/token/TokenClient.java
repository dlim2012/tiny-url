package com.dlim2012.clients.token;

import com.dlim2012.clients.token.dto.TokenItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "token",
        url = "${clients.token.url}"
)
public interface TokenClient {
    @GetMapping("/token")
    TokenItem getToken();
}
