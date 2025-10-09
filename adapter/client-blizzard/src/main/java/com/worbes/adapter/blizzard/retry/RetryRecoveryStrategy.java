package com.worbes.adapter.blizzard.retry;

public interface RetryRecoveryStrategy {
    boolean supports(Throwable e);

    void recover();
}
