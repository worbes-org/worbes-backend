package com.worbes.adapter.blizzard.retry;

import com.worbes.adapter.blizzard.client.BlizzardAccessTokenRestClient;
import com.worbes.adapter.blizzard.client.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("blizzard")
@RequiredArgsConstructor
class BlizzardTokenRefreshStrategy implements RetryRecoveryStrategy {

    private final BlizzardAccessTokenRestClient oauthClient;

    @Override
    public boolean supports(Throwable e) {
        return e instanceof UnauthorizedException;
    }

    @Override
    public void recover() {
        oauthClient.refresh();
    }
}
