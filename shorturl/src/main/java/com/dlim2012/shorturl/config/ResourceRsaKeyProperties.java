package com.dlim2012.shorturl.config;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "rsa")
public record ResourceRsaKeyProperties(RSAPublicKey publicKey) {
}
