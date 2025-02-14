package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.config.properties.OAuth2ConfigProperties;
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
@EnableConfigurationProperties(OAuth2ConfigProperties.class)
public class OAuth2Config {

    private final OAuth2ConfigProperties properties;

    @Bean
    public RestClient oauth2Client() {
        String encodedCredentials = getEncodedCredentials(properties.getId(), properties.getSecret());
        return RestClient.builder()
                .baseUrl(properties.getTokenUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", String.format("Basic %s", encodedCredentials))
                .build();
    }

    private String getEncodedCredentials(String id, String secret) {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
    }
}
