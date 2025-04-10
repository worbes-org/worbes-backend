package com.worbes.infra.blizzard.client;

import com.worbes.infra.rest.client.RestApiClient;
import com.worbes.infra.rest.factory.RestApiRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.RetryCallback;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BlizzardHttpClientTest {

    @Mock
    private RestApiClient restApiClient;

    @Mock
    private BlizzardAccessTokenHandler tokenHandler;

    @Mock
    private BlizzardRetryPolicy retryPolicy;

    @InjectMocks
    private BlizzardHttpClient httpClient;

    @Test
    @DisplayName("정상 호출 시 token + params + headers 포함되어 요청되어야 한다")
    void shouldCallRestApiClientThroughRetryPolicy() throws Exception {
        // given
        String url = "https://api.blizzard.com/data/wow/test";
        Map<String, String> queryParams = Map.of("namespace", "static-kr");
        String token = "access-token";

        given(tokenHandler.get()).willReturn(token);

        ArgumentCaptor<RetryCallback<String, Exception>> callbackCaptor =
                ArgumentCaptor.forClass(RetryCallback.class);

        given(retryPolicy.execute(callbackCaptor.capture())).willReturn("pong");

        // when
        String result = httpClient.get(url, queryParams, String.class);

        // then
        assertThat(result).isEqualTo("pong");

        RetryCallback<String, Exception> capturedCallback = callbackCaptor.getValue();
        capturedCallback.doWithRetry(null);

        then(tokenHandler).should().get();

        ArgumentCaptor<RestApiRequest> requestCaptor = ArgumentCaptor.forClass(RestApiRequest.class);
        then(restApiClient).should().get(requestCaptor.capture(), eq(String.class));

        RestApiRequest request = requestCaptor.getValue();
        assertThat(request.url()).isEqualTo(url);
        assertThat(request.queryParams()).containsEntry("namespace", "static-kr");
        assertThat(request.headers()).containsEntry("Authorization", "Bearer " + token);
    }
}
