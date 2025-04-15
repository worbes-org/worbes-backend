package com.worbes.infra.rest.core.retry;

import com.worbes.infra.rest.core.exception.RestApiClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DefaultRetryExecutor implements RetryExecutor {

    private final RetryTemplate template;
    private final List<RetryRecoveryStrategy> strategies;

    public <T> T execute(RetryCallback<T, RestApiClientException> callback) {
        return template.execute(context -> {
            handleRecovery(context.getLastThrowable());
            return callback.doWithRetry(context);
        });
    }

    private void handleRecovery(Throwable t) {
        strategies.stream()
                .filter(s -> s.supports(t))
                .findFirst()
                .ifPresent(RetryRecoveryStrategy::recover);
    }
}
