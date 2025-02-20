package com.worbes.auctionhousetracker.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.auctionhousetracker.config.properties.BlizzardApiConfigProperties;
import com.worbes.auctionhousetracker.dto.response.TokenResponse;
import com.worbes.auctionhousetracker.infrastructure.oauth.AccessTokenHandler;
import com.worbes.auctionhousetracker.infrastructure.oauth.AccessTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

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
@RestClientTest(AccessTokenHandler.class)
class AccessTokenHandlerImplTest {

    private static final String TOKEN_URL = "https://oauth.battle.net/token";
    @Autowired
    MockRestServiceServer server;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AccessTokenHandler tokenHandler;
    @MockBean
    AccessTokenRepository tokenRepository;
    @SpyBean
    BlizzardApiConfigProperties properties;

    @BeforeEach
    void setUp() {
        given(properties.getTokenUrl()).willReturn(TOKEN_URL);
        given(properties.getId()).willReturn("valid-id");
        given(properties.getSecret()).willReturn("valid-secret");
    }

    @Test
    @DisplayName("토큰이 이미 존재하면 그대로 반환")
    void getToken_WhenTokenExists_ShouldReturnToken() {
        // Given
        String token = "valid-token";
        given(tokenRepository.get(anyString())).willReturn(token);

        // When
        String result = tokenHandler.get();

        // Then
        assertThat(result).isEqualTo(token);
        verify(tokenRepository, never()).save(anyString(), anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("토큰이 없으면 새로 발급 요청")
    void getToken_WhenTokenNotExists_ShouldFetchNewToken() throws JsonProcessingException {
        // Given
        given(tokenRepository.get(anyString())).willReturn(null);

        TokenResponse mockResponse = new TokenResponse("new-access-token", "bearer", 3600L);
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);

        server.expect(requestTo(TOKEN_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // When
        String token = tokenHandler.get();

        // Then
        assertThat(token).isEqualTo(mockResponse.getAccessToken());
        verify(tokenRepository).save(
                anyString(),
                eq(mockResponse.getAccessToken()),
                eq(mockResponse.getExpiresIn()),
                eq(TimeUnit.SECONDS)
        );
    }

    @Test
    @DisplayName("블리자드 API 요청 실패 시 예외 발생")
    void refresh_WhenBlizzardApiFails_ShouldThrowException() {
        // Given
        given(tokenRepository.get(anyString())).willReturn(null);

        server.expect(requestTo(TOKEN_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        // When & Then
        assertThatThrownBy(() -> tokenHandler.get())
                .isInstanceOf(RuntimeException.class);
    }
}
