package com.worbes.infra.rest;

import com.worbes.infra.cache.CacheConfig;
import com.worbes.infra.rest.core.client.DefaultRestApiErrorHandler;
import com.worbes.infra.rest.core.client.RestApiErrorHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

@Configuration
@Import(CacheConfig.class)
@ComponentScan
public class RestConfig {

    @Bean
    @ConditionalOnMissingBean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestApiErrorHandler errorHandler() {
        return new DefaultRestApiErrorHandler();
    }
}
