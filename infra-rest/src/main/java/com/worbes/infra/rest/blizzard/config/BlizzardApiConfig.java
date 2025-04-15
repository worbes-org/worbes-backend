package com.worbes.infra.rest.blizzard.config;

import com.worbes.infra.rest.core.client.RestApiClient;
import com.worbes.infra.rest.core.client.RestClientAdapter;
import com.worbes.infra.rest.core.error.RestApiErrorHandler;
import com.worbes.infra.rest.core.exception.InternalServerErrorException;
import com.worbes.infra.rest.core.exception.TooManyRequestsException;
import com.worbes.infra.rest.core.exception.UnauthorizedException;
import com.worbes.infra.rest.core.retry.DefaultRetryExecutor;
import com.worbes.infra.rest.core.retry.RetryExecutor;
import com.worbes.infra.rest.core.retry.RetryRecoveryStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(BlizzardApiConfigProperties.class)
public class BlizzardApiConfig {

    @Bean
    public RestApiClient blizzardApiClient(RestClient.Builder builder, RestApiErrorHandler errorHandler) {
        builder.requestFactory(clientHttpRequestFactory());
        return new RestClientAdapter(builder, errorHandler);
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        return factory;
    }

    @Bean
    public RetryExecutor blizzardRetryExecutor(RetryTemplate template, List<RetryRecoveryStrategy> strategy) {
        return new DefaultRetryExecutor(template, strategy);
    }

    @Bean
    public RetryPolicy blizzardRetryPolicy() {
        ExceptionClassifierRetryPolicy classifier = new ExceptionClassifierRetryPolicy();
        classifier.setPolicyMap(Map.of(
                UnauthorizedException.class, new SimpleRetryPolicy(2),
                InternalServerErrorException.class, new SimpleRetryPolicy(2),
                TooManyRequestsException.class, new SimpleRetryPolicy(3)
        ));

        return classifier;
    }

    @Bean
    public BackOffPolicy blizzardBackOffPolicy() {
        ExponentialRandomBackOffPolicy backOff = new ExponentialRandomBackOffPolicy();
        backOff.setInitialInterval(1000);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(5000);
        return backOff;
    }

    @Bean
    public RetryTemplate blizzardRetryTemplate(RetryPolicy blizzardRetryPolicy, BackOffPolicy blizzardBackOffPolicy) {
        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(blizzardRetryPolicy);
        template.setBackOffPolicy(blizzardBackOffPolicy);
        return template;
    }
}
