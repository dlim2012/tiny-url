package com.dlim2012.longurl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(
        basePackages = "com.dlim2012.clients"
)
//@PropertySources({
//        @PropertySource("classpath:clients-${spring.profiles.active}.properties")
//})
public class LongUrlApplication {
    public static void main(String[] args) {
        SpringApplication.run(LongUrlApplication.class, args);
    }
}
