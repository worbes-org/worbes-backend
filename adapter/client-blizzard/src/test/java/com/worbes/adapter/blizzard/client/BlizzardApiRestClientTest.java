package com.worbes.adapter.blizzard.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.adapter.blizzard.BlizzardApiRestClientTestConfig;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("Integration::BlizzardApiRestClient")
@Import({BlizzardApiRestClientTestConfig.class})
@RestClientTest
class BlizzardApiRestClientTest {

    @MockBean
    private BlizzardAccessTokenHandler tokenHandler;

    @Autowired
    private BlizzardApiUriFactory uriFactory;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BlizzardApiRestClient apiClient;

    @Test
    @DisplayName("정상적인 fetch 호출 시 응답을 반환한다")
    void fetch_shouldReturnResponse_whenRequestIsValid() throws JsonProcessingException {
        // given
        String token = "test-token";
        URI uri = uriFactory.itemUri(123L);
        DummyResponse expected = new DummyResponse("hello");
        String responseBody = objectMapper.writeValueAsString(expected);

        given(tokenHandler.get()).willReturn(token);

        server.expect(requestTo(uri))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // when
        DummyResponse actual = apiClient.fetch(uri, DummyResponse.class);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("401 오류 상태 코드일 경우 errorHandler가 호출되어 UnauthorizedException 예외를 던진다")
    void fetch_shouldThrowUnauthorizedException_whenErrorResponseAndHandlerInvoked() {
        // given
        URI uri = uriFactory.realmIndexUri(RegionType.KR);
        given(tokenHandler.get()).willReturn("token");

        server.expect(requestTo(uri))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        // when & then
        assertThatThrownBy(() -> apiClient.fetch(uri, DummyResponse.class))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("429 오류 상태 코드일 경우 errorHandler가 호출되어 TooManyRequestsException 예외를 던진다")
    void fetch_shouldThrowTooManyRequestsException_whenErrorResponseAndHandlerInvoked() {
        // given
        URI uri = uriFactory.realmIndexUri(RegionType.KR);
        given(tokenHandler.get()).willReturn("token");

        server.expect(requestTo(uri))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        // when & then
        assertThatThrownBy(() -> apiClient.fetch(uri, DummyResponse.class))
                .isInstanceOf(TooManyRequestsException.class);
    }

    @Test
    @DisplayName("500 오류 상태 코드일 경우 errorHandler가 호출되어 InternalServerErrorException 예외를 던진다")
    void fetch_shouldThrowInternalServerErrorException_whenErrorResponseAndHandlerInvoked() {
        // given
        URI uri = uriFactory.auctionUri(RegionType.KR, 205L);
        given(tokenHandler.get()).willReturn("token");

        server.expect(requestTo(uri))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // when & then
        assertThatThrownBy(() -> apiClient.fetch(uri, DummyResponse.class))
                .isInstanceOf(InternalServerErrorException.class);
    }

    @Test
    @DisplayName("정의 되지 않은 오류 상태 코드일 경우 errorHandler가 호출되어 BlizzardApiException 예외를 던진다")
    void fetch_shouldThrowInternalBlizzardApiException_whenErrorResponseAndHandlerInvoked() {
        // given
        URI uri = uriFactory.auctionUri(RegionType.KR, 205L);
        given(tokenHandler.get()).willReturn("token");

        server.expect(requestTo(uri))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // when & then
        assertThatThrownBy(() -> apiClient.fetch(uri, DummyResponse.class))
                .isInstanceOf(BlizzardApiException.class);
    }

    @Test
    @DisplayName("fetchAsync는 비동기로 응답을 반환한다")
    void fetchAsync_shouldReturnAsyncResult() throws Exception {
        // given
        String token = "token";
        URI uri = uriFactory.itemSubclassUri(0L, 10L);
        DummyResponse expected = new DummyResponse("async");
        String responseBody = objectMapper.writeValueAsString(expected);

        given(tokenHandler.get()).willReturn(token);

        server.expect(requestTo(uri))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // when
        CompletableFuture<DummyResponse> future = apiClient.fetchAsync(uri, DummyResponse.class);
        DummyResponse actual = future.get(3, TimeUnit.SECONDS);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private record DummyResponse(String value) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DummyResponse that)) return false;
            return Objects.equals(value, that.value);
        }
    }
}
