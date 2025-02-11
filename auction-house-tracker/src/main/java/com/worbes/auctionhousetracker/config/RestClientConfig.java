package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.config.properties.RestClientConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RestClientConfigProperties.class)
public class RestClientConfig {

    private final RestClientConfigProperties properties;

    @Bean
    public RestClient apiClient() {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public RestClient oauth2Client() {
        String encodedCredentials = getEncodedCredentials(properties.getId(), properties.getSecret());
        return RestClient.builder()
                .baseUrl(properties.getTokenUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader("Authorization", String.format("Basic %s", encodedCredentials))
                .build();
    }

    private String getEncodedCredentials(String id, String secret) {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
    }
}
