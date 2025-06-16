package com.worbes.adapter.blizzard;

import com.worbes.adapter.blizzard.client.*;
import com.worbes.adapter.blizzard.retry.BlizzardRetryableApiClient;
import com.worbes.adapter.blizzard.retry.RetryExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(BlizzardConfigPropertiesImpl.class)
public class BlizzardClientConfig {

    @Bean
    public BlizzardApiClient blizzardApiClient(
            RestClient.Builder builder,
            BlizzardAccessTokenHandler tokenHandler,
            RestClientErrorHandler errorHandler,
            RetryExecutor retryExecutor,
            @Qualifier("blizzardClientExecutor") ThreadPoolTaskExecutor threadExecutor
    ) {
        builder.requestFactory(clientHttpRequestFactory());
        BlizzardApiRestClient restClient = new BlizzardApiRestClient(builder, tokenHandler, errorHandler, threadExecutor);
        return new BlizzardRetryableApiClient(retryExecutor, restClient);
    }

    @Bean
    public BlizzardAccessTokenHandler blizzardAccessTokenHandler(
            BlizzardConfigProperties properties,
            RestClient.Builder builder,
            RestClientErrorHandler errorHandler,
            BlizzardAccessTokenCache cache
    ) {
        builder.requestFactory(clientHttpRequestFactory());
        return new BlizzardAccessTokenHandlerImpl(properties, builder, errorHandler, cache);
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        return factory;
    }

    @Bean(name = "blizzardClientExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setKeepAliveSeconds(60);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("blizzardClientExecutor-");
        executor.initialize();
        return executor;
    }
}
