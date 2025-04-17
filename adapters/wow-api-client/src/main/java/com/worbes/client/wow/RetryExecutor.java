package com.worbes.client.wow;

public interface RetryExecutor {
    <T> T execute(RetryableWithContext<T> callback);
}
