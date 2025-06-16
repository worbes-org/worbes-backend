package com.worbes.adapter.blizzard;

import com.worbes.adapter.blizzard.client.InternalServerErrorException;
import com.worbes.adapter.blizzard.client.TooManyRequestsException;
import com.worbes.adapter.blizzard.client.UnauthorizedException;
import com.worbes.adapter.blizzard.retry.DefaultRetryExecutor;
import com.worbes.adapter.blizzard.retry.RetryExecutor;
import com.worbes.adapter.blizzard.retry.RetryRecoveryStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.List;
import java.util.Map;

@Configuration
@EnableRetry
public class BlizzardRetryConfig {

    @Bean
    public RetryExecutor blizzardRetryExecutor(
            RetryTemplate template,
            @Qualifier("blizzard") List<RetryRecoveryStrategy> strategy
    ) {
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
