package com.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.redis")
@Data
public class RedisProperties {
    private Integer port;
    private String host;
    private String password;
}
