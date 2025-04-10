package com.worbes.infra.blizzard.client;

import com.worbes.infra.rest.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlizzardRetryPolicy {

    private final BlizzardAccessTokenHandler accessTokenHandler;
    private final RetryTemplate blizzardRetryTemplate;

    public <T> T execute(RetryCallback<T, Exception> callback) {
        try {
            return blizzardRetryTemplate.execute(context -> {
                handleUnauthorizedIfPresent(context);
                return callback.doWithRetry(context);
            });
        } catch (Exception e) {
            throw new IllegalStateException("Retry 처리 중 예외 발생", e);
        }
    }

    private void handleUnauthorizedIfPresent(RetryContext context) {
        if (context.getLastThrowable() instanceof UnauthorizedException) {
            accessTokenHandler.refresh();
        }
    }
}
