package com.worbes.infra.rest.core.client;

import com.worbes.infra.rest.core.exception.RestApiClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(RestClientAdapter.class)
@ContextConfiguration(classes = {RestClientAdapter.class, DefaultRestApiErrorHandler.class})
class RestClientAdapterTest {

    @Autowired
    MockRestServiceServer server;

    @Autowired
    private RestClientAdapter client;

    @SpyBean
    private DefaultRestApiErrorHandler errorHandler;

    @Test
    @DisplayName("GET 요청이 정상적으로 전송되고 응답을 반환해야 한다")
    void shouldSendGetRequestAndReturnResponse() {
        // given
        RequestParams request = RequestParams.builder()
                .url("https://api.example.com/items")
                .queryParams(Map.of("namespace", "static-kr"))
                .headers(Map.of("Authorization", "Bearer test-token"))
                .build();

        server.expect(once(), requestTo("https://api.example.com/items?namespace=static-kr"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
                .andRespond(withSuccess("pong", MediaType.APPLICATION_JSON));

        // when
        String result = client.get(request, String.class);

        // then
        assertThat(result).isEqualTo("pong");
    }

    @Test
    @DisplayName("GET 요청 실패 시 핸들러가 호출되고 예외가 던져져야 한다")
    void shouldCallErrorHandlerOnFailure() {
        // given
        RequestParams request = RequestParams.builder()
                .url("https://api.example.com/error")
                .headers(Map.of("Authorization", "Bearer test-token"))
                .build();

        server.expect(requestTo("https://api.example.com/error"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.I_AM_A_TEAPOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\": \"tea\"}"));

        // when & then
        assertThatThrownBy(() -> client.get(request, String.class))
                .isInstanceOf(RestApiClientException.class);

        then(errorHandler).should().handle(any(), any());
    }

    @Test
    @DisplayName("POST 요청이 body와 함께 전송되고 응답을 반환해야 한다")
    void shouldSendPostWithBodyAndReturnResponse() {
        // given
        RequestParams request = RequestParams.builder()
                .url("https://api.example.com/post")
                .queryParams(Map.of("lang", "ko"))
                .headers(Map.of("Authorization", "Bearer abc123"))
                .body(Map.of("key", "value"))
                .build();

        server.expect(once(), requestTo("https://api.example.com/post?lang=ko"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer abc123"))
                .andExpect(content().json("{\"key\":\"value\"}"))
                .andRespond(withSuccess("ok", MediaType.APPLICATION_JSON));

        // when
        String response = client.post(request, String.class);

        // then
        assertThat(response).isEqualTo("ok");
    }

    @Test
    @DisplayName("POST 요청 실패 시 핸들러가 호출되고 예외가 발생해야 한다")
    void shouldCallErrorHandlerOnPostFailure() {
        // given
        RequestParams request = RequestParams.builder()
                .url("https://api.example.com/fail-post")
                .headers(Map.of("Authorization", "Bearer post-token"))
                .queryParams(Map.of("q", "test"))
                .body(Map.of("hello", "world"))
                .build();

        server.expect(requestTo("https://api.example.com/fail-post?q=test"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(content().json("{\"hello\":\"world\"}"))
                .andRespond(withStatus(HttpStatus.I_AM_A_TEAPOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\": \"brew failed\"}"));

        // when & then
        assertThatThrownBy(() -> client.post(request, String.class))
                .isInstanceOf(RestApiClientException.class);

        then(errorHandler).should().handle(any(), any());
    }
}
