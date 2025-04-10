package com.worbes.infra.blizzard.config;

import com.worbes.infra.rest.exception.InternalServerErrorException;
import com.worbes.infra.rest.exception.NotFoundException;
import com.worbes.infra.rest.exception.TooManyRequestsException;
import com.worbes.infra.rest.exception.UnauthorizedException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(BlizzardApiConfigProperties.class)
public class BlizzardApiConfig {

    @Bean
    public RetryPolicy blizzardRetryPolicy() {
        ExceptionClassifierRetryPolicy classifier = new ExceptionClassifierRetryPolicy();
        classifier.setPolicyMap(Map.of(
                UnauthorizedException.class, new SimpleRetryPolicy(2),
                TooManyRequestsException.class, new SimpleRetryPolicy(3),
                InternalServerErrorException.class, new SimpleRetryPolicy(2),
                NotFoundException.class, new NeverRetryPolicy()
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
