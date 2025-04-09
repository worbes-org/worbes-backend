package com.worbes.infra.blizzard.client;

import com.worbes.infra.rest.common.client.AccessTokenHandler;
import com.worbes.infra.rest.common.client.RestApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlizzardAccessTokenHandler implements AccessTokenHandler {

    private final RestApiClient restApiClient;


    @Override
    public String get() {
        return "";
    }

    @Override
    public String refresh() {
        return "";
    }
}
