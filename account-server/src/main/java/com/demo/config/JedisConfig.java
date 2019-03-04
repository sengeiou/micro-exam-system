package com.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class JedisConfig {
    @Autowired
    private RedisProperties redisProperties;
    @Bean
    public Jedis getJedis(){
        String host = redisProperties.getHost();
        JedisPool pool=new JedisPool(host);
        Jedis jedis = pool.getResource();
        jedis.auth(redisProperties.getPassword());
        return jedis;
    }
}
