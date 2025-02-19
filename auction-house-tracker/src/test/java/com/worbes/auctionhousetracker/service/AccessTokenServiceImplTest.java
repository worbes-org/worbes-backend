package com.worbes.auctionhousetracker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.auctionhousetracker.config.properties.OAuth2ConfigProperties;
import com.worbes.auctionhousetracker.dto.response.TokenResponse;
import com.worbes.auctionhousetracker.oauth2.AccessTokenService;
import com.worbes.auctionhousetracker.oauth2.AccessTokenServiceImpl;
import com.worbes.auctionhousetracker.repository.AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@Slf4j
@ActiveProfiles("test")
@RestClientTest(AccessTokenServiceImpl.class)
@EnableConfigurationProperties(OAuth2ConfigProperties.class)
class AccessTokenServiceImplTest {

    @Autowired
    OAuth2ConfigProperties properties;

    @Autowired
    MockRestServiceServer server;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccessTokenService tokenService;

    @MockBean
    AccessTokenRepository tokenRepository;

    @Test
    @DisplayName("토큰이 이미 존재하면 그대로 반환")
    void getToken_WhenTokenExists_ShouldReturnToken() {
        // Given
        given(tokenRepository.get("VALID-KEY")).willReturn("valid-token");

        // When
        String token = tokenService.get();

        // Then
        assertThat(token).isEqualTo("valid-token");
        verify(tokenRepository, never()).save(anyString(), anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("토큰이 없으면 새로 발급 요청")
    void getToken_WhenTokenNotExists_ShouldFetchNewToken() throws JsonProcessingException {
        // Given
        given(tokenRepository.get("VALID-KEY")).willReturn(null);

        TokenResponse mockResponse = new TokenResponse("new-access-token", "bearer", 3600L);
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);

        server.expect(requestTo(properties.getTokenUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // When
        String token = tokenService.get();

        // Then
        assertThat(token).isEqualTo("new-access-token");
        verify(tokenRepository).save(eq("VALID-KEY"), eq("new-access-token"), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("블리자드 API 요청 실패 시 예외 발생")
    void refresh_WhenBlizzardApiFails_ShouldThrowException() {
        // Given
        given(tokenRepository.get("VALID-KEY")).willReturn(null);

        server.expect(requestTo(properties.getTokenUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        // When & Then
        assertThatThrownBy(() -> tokenService.get())
                .isInstanceOf(RuntimeException.class);
    }

    @TestConfiguration
    @RequiredArgsConstructor
    @EnableConfigurationProperties(OAuth2ConfigProperties.class)
    static class TestConfig {

        private final OAuth2ConfigProperties properties;

        @Bean
        RestClient oauth2Client(RestClient.Builder builder) {
            return builder.baseUrl(properties.getTokenUrl()).build();
        }
    }
}
