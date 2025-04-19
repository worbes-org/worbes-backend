package com.worbes.adapter.blizzard.client;

import com.worbes.adapter.blizzard.cache.BlizzardAccessTokenCache;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(BlizzardApiPropertiesImpl.class)
class BlizzardApiConfig {

    @Bean
    public BlizzardApiClient blizzardApiClient(
            RestClient.Builder builder,
            BlizzardAccessTokenClient tokenHandler,
            RestClientErrorHandler errorHandler
    ) {
        builder.requestFactory(clientHttpRequestFactory());
        return new BlizzardApiRestClient(builder, tokenHandler, errorHandler);
    }

    @Bean
    public BlizzardAccessTokenClient blizzardAccessTokenClient(
            BlizzardApiProperties properties,
            RestClient.Builder builder,
            RestClientErrorHandler errorHandler,
            BlizzardAccessTokenCache cache
    ) {
        builder.requestFactory(clientHttpRequestFactory());
        return new BlizzardAccessTokenRestClient(properties, builder, errorHandler, cache);
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        return factory;
    }
}
