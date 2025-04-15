package com.worbes.infra.rest.core.config;

import com.worbes.infra.rest.core.error.DefaultRestApiErrorHandler;
import com.worbes.infra.rest.core.error.RestApiErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestApiErrorHandler errorHandler() {
        return new DefaultRestApiErrorHandler();
    }
}
