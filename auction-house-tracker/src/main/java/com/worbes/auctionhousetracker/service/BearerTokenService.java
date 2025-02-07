package com.worbes.auctionhousetracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BearerTokenService {

    @Value("${blizzard.api.token}")
    private String apiToken;

    public String getToken() {
        return "Bearer " + apiToken;
    }
}
