package com.dlim2012.longurl;

import com.dlim2012.longurl.repository.ShortPathToLongRepository;
import com.dlim2012.longurl.entity.ShortPathToLong;
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
public class LongURLApplication {
    public static void main(String[] args) {
        SpringApplication.run(LongURLApplication.class, args);
    }

    private final ShortPathToLongRepository shortPathToLongRepository;

//    @Bean
//    CommandLineRunner commandLineRunner(){
//        return args -> {
//            shortPathToLongRepository.save(new ShortPathToLong("1", "1111"));
//            shortPathToLongRepository.save(new ShortPathToLong("2", "2222"));
//            shortPathToLongRepository.save(new ShortPathToLong("3", "3333"));
//            shortPathToLongRepository.save(new ShortPathToLong("4", "4444"));
//        };
//    }


}
