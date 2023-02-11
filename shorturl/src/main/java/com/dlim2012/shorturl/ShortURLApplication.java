package com.dlim2012.shorturl;

import com.dlim2012.shorturl.repository.LongToShortPathRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.core.CassandraOperations;

@Slf4j
@SpringBootApplication
@EnableFeignClients(
        basePackages = "com.dlim2012.clients"
)
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class ShortUrlApplication {
    public static void main(String[] args){
        SpringApplication.run(ShortUrlApplication.class, args);
    }

    @Autowired
    private final ShortUrlService shortURLService;

    public ShortUrlApplication(ShortUrlService shortURLService) {
        this.shortURLService = shortURLService;
    }

    @Bean
    CommandLineRunner commandLineRunner(){
        return args -> {
            shortURLService.generateShortURLAndSave("https://github.com/dlim2012/tiny-url-system-design");
        };
    }

}
