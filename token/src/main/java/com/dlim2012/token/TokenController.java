package com.dlim2012.token;

import com.dlim2012.clients.token.dto.TokenItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("api/v1/token")
@AllArgsConstructor
//@FeignClient("token")
public class TokenController {
    private final TokenService tokenService;

    @GetMapping
    public TokenItem getToken(){
        TokenItem tokenItem = tokenService.getToken();
        log.info("Get token: " + tokenItem.toString());
        return tokenItem;
    }
}
