package com.worbes.adapter.blizzard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WowHeadClientConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}
