package com.worbes.auctionhousetracker.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EnvBearerTokenHandler implements BearerTokenHandler{

    @Value("${blizzard.api.token}")
    private String apiToken;

    @Override
    public String getToken() {
        return "Bearer " + apiToken;
    }
}
