package com.friday.tool.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author e99g41y
 * @date 2022/8/15 10:25
 */
@Slf4j
@Configuration
public class RedissonConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        redisStandaloneConfiguration.setUsername(redisProperties.getUsername());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        if (redisProperties.getCluster() == null) {
            config.useSingleServer()
                    .setAddress(String.format("redis://%s:%d", redisProperties.getHost(), redisProperties.getPort()))
                    .setPassword(redisProperties.getPassword())
                    .setUsername(redisProperties.getUsername());
        } else {
            List<String> nodes = redisProperties.getCluster().getNodes().stream().map(s -> "redis://" + s).collect(Collectors.toList());
            config.useClusterServers()
                    .setPassword(redisProperties.getPassword())
                    .setUsername(redisProperties.getUsername())
                    .setNodeAddresses(nodes);
        }
        return Redisson.create(config);
    }
}
