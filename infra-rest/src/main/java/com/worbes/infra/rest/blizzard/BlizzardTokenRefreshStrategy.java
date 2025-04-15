package com.worbes.infra.rest.blizzard;

import com.worbes.infra.rest.core.exception.UnauthorizedException;
import com.worbes.infra.rest.core.retry.RetryRecoveryStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("blizzard")
@RequiredArgsConstructor
class BlizzardTokenRefreshStrategy implements RetryRecoveryStrategy {
    private final BlizzardAccessTokenHandler handler;

    @Override
    public boolean supports(Throwable e) {
        return e instanceof UnauthorizedException;
    }

    @Override
    public void recover() {
        handler.refresh();
    }
}
