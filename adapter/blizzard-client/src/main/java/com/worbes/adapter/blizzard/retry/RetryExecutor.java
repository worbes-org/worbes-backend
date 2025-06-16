package com.worbes.adapter.blizzard.retry;

import org.springframework.retry.RetryCallback;

public interface RetryExecutor {
    <T, E extends RuntimeException> T execute(RetryCallback<T, E> callback);
}
