package com.worbes.adapter.blizzard.retry;

import lombok.RequiredArgsConstructor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

import java.util.List;

@RequiredArgsConstructor
public class DefaultRetryExecutor implements RetryExecutor {

    private final RetryTemplate template;
    private final List<RetryRecoveryStrategy> strategies;

    @Override
    public <T, E extends RuntimeException> T execute(RetryCallback<T, E> callback) {
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
