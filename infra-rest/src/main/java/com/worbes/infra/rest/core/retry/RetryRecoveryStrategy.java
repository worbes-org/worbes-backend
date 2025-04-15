package com.worbes.infra.rest.core.retry;

public interface RetryRecoveryStrategy {
    boolean supports(Throwable e);

    void recover();
}
