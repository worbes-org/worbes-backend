package com.worbes.client.wow;

@FunctionalInterface
public interface RetryableWithContext<T> {
    T call(RetryContextWrapper context);
}
