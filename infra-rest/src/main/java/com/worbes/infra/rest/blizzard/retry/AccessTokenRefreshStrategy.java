package com.worbes.infra.rest.blizzard.retry;

import com.worbes.infra.rest.blizzard.auth.AccessTokenHandler;
import com.worbes.infra.rest.core.exception.UnauthorizedException;
import com.worbes.infra.rest.core.retry.RetryRecoveryStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessTokenRefreshStrategy implements RetryRecoveryStrategy {
    private final AccessTokenHandler handler;

    @Override
    public boolean supports(Throwable e) {
        return e instanceof UnauthorizedException;
    }

    @Override
    public void recover() {
        handler.refresh();
    }
}
