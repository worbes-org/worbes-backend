package com.worbes.infra.rest.client;

import com.worbes.infra.rest.exception.RestApiClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Component
public class RestApiClientImpl implements RestApiClient {

    private final RestClient restClient;
    private final RestApiErrorHandler errorHandler;
    private final AccessTokenHandler accessTokenHandler;

    public RestApiClientImpl(
            RestClient.Builder builder,
            AccessTokenHandler accessTokenHandler,
            RestApiErrorHandler errorHandler
    ) {
        this.restClient = builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.accessTokenHandler = accessTokenHandler;
        this.errorHandler = errorHandler;
    }

    @Retryable(
            recover = "recover",
            noRetryFor = NoSuchElementException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 5000, random = true)
    )
    @Override
    public <T> T get(String url, Map<String, String> params, Class<T> responseType, boolean withAuth) {
        Map<String, String> headers = createAuthHeaders(withAuth);

        return restClient.get()
                .uri(createUri(url, params))
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handle)
                .body(responseType);
    }

    @Override
    public <T> T get(String url, Map<String, String> params, Class<T> responseType) {
        return get(url, params, responseType, false);
    }

    @Override
    @Retryable(
            recover = "recover",
            noRetryFor = NoSuchElementException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 5000, random = true)
    )
    public <T, R> R post(String url, Map<String, String> params, T body, Class<R> responseType, boolean withAuth) {
        Map<String, String> headers = createAuthHeaders(withAuth);

        return restClient.post()
                .uri(createUri(url, params))
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handle)
                .body(responseType);
    }

    @Override
    public <T, R> R post(String url, Map<String, String> params, T body, Class<R> responseType) {
        return post(url, params, body, responseType, false);
    }

    private URI createUri(String url, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        params.forEach(builder::queryParam);
        return builder.build().toUri();
    }

    private Map<String, String> createAuthHeaders(boolean withAuth) {
        if (!withAuth) return Map.of();
        return Map.of("Authorization", String.format("Bearer %s", accessTokenHandler.get()));
    }

    @Recover
    public <T> T recover(RestApiClientException e, String path, Map<String, String> queryParams, Class<T> responseType) {
        log.error("최대 재시도 초과 | 요청 경로: {} | 상태 코드: {} | 메시지: {}", path, e.getStatusCode(), e.getMessage());
        //TODO: 문제가 생긴 요청은 무시하고 경매 데이터 저장
        return null;
    }
}
