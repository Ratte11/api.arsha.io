package io.arsha.api.config;

import io.arsha.api.config.services.CacheConfigurationService;
import io.arsha.api.data.CacheCompositeKey;
import io.arsha.api.data.market.MarketResponse;
import io.arsha.api.data.scraper.ScrapedItem;
import io.arsha.api.lib.CacheCompositeKeyRedisSerializer;
import io.arsha.api.lib.MarketResponseValueRedisSerializer;
import io.arsha.api.lib.ScrapedItemValueRedisSerializer;

import java.util.concurrent.ExecutorService;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.RedisKeyValueAdapter.EnableKeyspaceEvents;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@EnableRedisRepositories(enableKeyspaceEvents = EnableKeyspaceEvents.OFF)
public class RedisConfiguration {

    @Bean
    public RedissonClient redissonClient(
            CacheConfigurationService properties,
            ExecutorService asyncExecutor) {

        var redisProperties = properties.getRedisConfig();

        var config = new Config();

        var host = "redis://%s:%d".formatted(
                redisProperties.getHost(),
                redisProperties.getPort()
        );

        var password = redisProperties.getPassword();

        config.setExecutor(asyncExecutor)
                .useSingleServer()
                .setAddress(host)
                .setPassword(password);

        return Redisson.create(config);
    }

    @Bean
    public RedissonConnectionFactory redissonConnectionFactory(
            RedissonClient redissonClient) {

        return new RedissonConnectionFactory(redissonClient);
    }

    @Bean
    public RedisTemplate<CacheCompositeKey, MarketResponse> marketRedisTemplate(
            RedisConnectionFactory cf) {

        var template =
                new RedisTemplate<CacheCompositeKey, MarketResponse>();

        template.setConnectionFactory(cf);

        template.setHashKeySerializer(
                new CacheCompositeKeyRedisSerializer()
        );

        template.setKeySerializer(
                new CacheCompositeKeyRedisSerializer()
        );

        template.setValueSerializer(
                new MarketResponseValueRedisSerializer()
        );

        template.setHashValueSerializer(
                new MarketResponseValueRedisSerializer()
        );

        return template;
    }

    @Bean
    public RedisTemplate<String, ScrapedItem> dbRedisTemplate(
            RedisConnectionFactory cf) {

        var template =
                new RedisTemplate<String, ScrapedItem>();

        template.setConnectionFactory(cf);

        template.setHashKeySerializer(
                new StringRedisSerializer()
        );

        template.setKeySerializer(
                new StringRedisSerializer()
        );

        template.setValueSerializer(
                new ScrapedItemValueRedisSerializer()
        );

        template.setHashValueSerializer(
                new ScrapedItemValueRedisSerializer()
        );

        return template;
    }
}
