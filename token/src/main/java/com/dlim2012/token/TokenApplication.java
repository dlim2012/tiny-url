package com.dlim2012.token;

import com.dlim2012.token.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@SpringBootApplication
@EnableFeignClients(
        basePackages = "coms.dlim2012.clients"
)
public class TokenApplication {
    public static void main(String[] args){
        SpringApplication.run(TokenApplication.class, args);
    }

    // TODO: CREATE AN MYSQL EVENT TO REMOVE OLD RECORDS EVERY DAY WITH LOW PRIORITY
}
