package com.worbes.client.wow;

public interface RetryContextWrapper {
    int getRetryCount();

    Throwable getLastThrowable();
}
