package com.worbes.adapter.blizzard;

import com.worbes.adapter.blizzard.client.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

@TestConfiguration
@Import({BlizzardApiErrorHandler.class, BlizzardCacheConfig.class, CaffeineAccessTokenCache.class})
@EnableConfigurationProperties(BlizzardConfigPropertiesImpl.class)
public class BlizzardAccessTokenHandlerTestConfig {

    @Bean
    public BlizzardAccessTokenHandler blizzardAccessTokenHandler(
            BlizzardConfigProperties properties,
            RestClient.Builder builder,
            RestClientErrorHandler errorHandler,
            BlizzardAccessTokenCache cache
    ) {
        return new BlizzardAccessTokenHandlerImpl(properties, builder, errorHandler, cache);
    }
}
