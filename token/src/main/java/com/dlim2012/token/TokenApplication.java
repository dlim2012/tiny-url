package com.dlim2012.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication
@EnableFeignClients(
        basePackages = "coms.dlim2012.clients"
)
public class TokenApplication {
    public static void main(String[] args){
        SpringApplication.run(TokenApplication.class, args);
    }
}
