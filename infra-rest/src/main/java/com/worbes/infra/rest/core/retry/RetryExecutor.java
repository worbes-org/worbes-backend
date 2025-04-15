package com.worbes.infra.rest.core.retry;

import com.worbes.infra.rest.core.exception.RestApiClientException;
import org.springframework.retry.RetryCallback;

public interface RetryExecutor {
    <T> T execute(RetryCallback<T, RestApiClientException> callback);
}
