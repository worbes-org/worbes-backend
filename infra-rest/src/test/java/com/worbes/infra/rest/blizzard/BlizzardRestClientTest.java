package com.worbes.infra.rest.blizzard;

import com.worbes.infra.rest.core.client.RequestParams;
import com.worbes.infra.rest.core.client.RestApiClient;
import com.worbes.infra.rest.core.retry.RetryExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BlizzardRestClientTest {

    @Mock
    private RetryExecutor retryExecutor;
    @Mock
    private RestApiClient client;
    @Mock
    private BlizzardAccessTokenHandler tokenHandler;

    private BlizzardRestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = new BlizzardRestClient(retryExecutor, client, tokenHandler);
    }

    @Test
    @DisplayName("정상 요청 시 tokenHandler.get()과 client.get()이 호출되어야 한다")
    void shouldCallTokenHandlerAndClientOnSuccess() {
        // given
        String url = "https://api.blizzard.com/item";
        Map<String, String> queryParams = Map.of("id", "123");
        String expectedToken = "mocked-token";
        String expectedResponse = "response";

        given(tokenHandler.get()).willReturn(expectedToken);
        given(retryExecutor.execute(any())).willAnswer(invocation -> {
            RetryCallback<Object, ?> callback = invocation.getArgument(0);
            return callback.doWithRetry(mock(RetryContext.class));
        });
        given(client.get(any(RequestParams.class), eq(String.class)))
                .willReturn(expectedResponse);

        // when
        String result = restClient.fetch(url, queryParams, String.class);

        // then
        then(tokenHandler).should().get();
        then(client).should().get(argThat(params ->
                params.url().equals(url) &&
                        params.queryParams().equals(queryParams) &&
                        params.headers().get(HttpHeaders.AUTHORIZATION).equals("Bearer " + expectedToken)
        ), eq(String.class));

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("RestApiClient.get()에서 예외가 발생하면 그대로 전파된다")
    void shouldPropagateExceptionOnFailure() {
        // given
        given(tokenHandler.get()).willReturn("token");
        given(retryExecutor.execute(any())).willAnswer(invocation -> {
            RetryCallback<Object, ?> callback = invocation.getArgument(0);
            return callback.doWithRetry(mock(RetryContext.class));
        });
        given(client.get(any(), eq(String.class)))
                .willThrow(new RuntimeException("API 실패"));

        // when & then
        assertThatThrownBy(() -> restClient.fetch("url", Map.of(), String.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("API 실패");

        then(tokenHandler).should().get();
        then(client).should().get(any(RequestParams.class), eq(String.class));
    }
}

