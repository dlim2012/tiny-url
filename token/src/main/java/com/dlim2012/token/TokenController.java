package com.dlim2012.token;

import com.dlim2012.clients.token.config.TokenConfiguration;
import com.dlim2012.clients.token.dto.TokenItem;
import com.dlim2012.clients.token.dto.TokenSetting;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("api/v1/token")
@AllArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    @GetMapping
    public TokenItem getToken(){
        TokenItem tokenItem = tokenService.getToken();
        log.info("Get token: " + tokenItem.toString());
        return tokenItem;
    }

//    @GetMapping("settings")
//    public TokenConfiguration getTokenSetting(){
//        TokenSetting tokenSetting = tokenService.getTokenSetting();
//        log.info("Get token setting: " + tokenSetting.toString());
//        return tokenSetting;
//    }
}
