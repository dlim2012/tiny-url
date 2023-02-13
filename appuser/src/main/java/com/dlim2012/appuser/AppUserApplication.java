package com.dlim2012.appuser;

import com.dlim2012.appuser.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
@EnableFeignClients(
		basePackages = "com.dlim2012.clients"
)

//todo: removable?
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class AppUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppUserApplication.class, args);
	}

}
