package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.config.properties.RestClientConfigProperties;
import com.worbes.auctionhousetracker.service.BearerTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RestClientConfigProperties.class)
public class RestClientConfig {

    private final RestClientConfigProperties properties;
    private final BearerTokenService bearerTokenService;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, bearerTokenService.getToken())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
