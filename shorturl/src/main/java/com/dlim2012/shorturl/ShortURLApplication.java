package com.dlim2012.shorturl;

import com.dlim2012.shorturl.config.ResourceRsaKeyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

@Slf4j
@EnableConfigurationProperties(ResourceRsaKeyProperties.class)
@SpringBootApplication
@EnableFeignClients(
        basePackages = "com.dlim2012.clients"
)
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class ShortUrlApplication {
    public static void main(String[] args){
        SpringApplication.run(ShortUrlApplication.class, args);
    }

}
