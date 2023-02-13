package com.dlim2012.shorturl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

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

//    @Autowired
//    private final ShortUrlService shortURLService;
//
//    public ShortUrlApplication(ShortUrlService shortURLService) {
//        this.shortURLService = shortURLService;
//    }

//    @Bean
//    CommandLineRunner commandLineRunner(){
//        return args -> {
//            shortURLService.generateShortURLAndSave("https://github.com/dlim2012/tiny-url-system-design");
//        };
//    }

}
