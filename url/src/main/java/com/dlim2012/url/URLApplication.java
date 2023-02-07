package com.dlim2012.url;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication
@EnableFeignClients(
        basePackages = "com.dlim2012.clients"
)
public class URLApplication {
    public static void main(String[] args){
        SpringApplication.run(URLApplication.class, args);
    }
}
