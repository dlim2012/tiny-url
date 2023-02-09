package com.dlim2012.shorturl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@EnableFeignClients(
        basePackages = "com.dlim2012.clients"
)
@EnableEurekaClient
@AllArgsConstructor
public class ShortURLApplication {
    public static void main(String[] args){
        SpringApplication.run(ShortURLApplication.class, args);
    }

    private final ShortURLService shortURLService;
    @Bean
    CommandLineRunner commandLineRunner(){
        return args -> {
            shortURLService.generateShortURL("www.google.com");

        };
    }

}
