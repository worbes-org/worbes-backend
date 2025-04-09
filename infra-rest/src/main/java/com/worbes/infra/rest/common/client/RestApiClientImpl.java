package com.worbes.infra.rest.common.client;

import com.worbes.infra.rest.exception.RestApiClientException;
import com.worbes.infra.rest.factory.GetRequestBuilder;
import com.worbes.infra.rest.factory.PostRequestBuilder;
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

    public RestApiClientImpl(RestClient.Builder builder, RestApiErrorHandler errorHandler) {
        this.restClient = builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.errorHandler = errorHandler;
    }

    @Override
    @Retryable(
            recover = "recover",
            noRetryFor = NoSuchElementException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 5000, random = true)
    )
    public <T> T get(GetRequestBuilder request, Class<T> response) {
        URI uri = createUri(request.url(), request.queryParams());
        return restClient.get()
                .uri(uri)
                .headers(httpHeaders -> request.headers().forEach(httpHeaders::add))
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handle)
                .body(response);
    }

    @Override
    @Retryable(
            recover = "recover",
            noRetryFor = NoSuchElementException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 5000, random = true)
    )
    public <T, R> R post(PostRequestBuilder<T> request, Class<R> response) {
        URI uri = createUri(request.url(), request.queryParams());
        return restClient.post()
                .uri(uri)
                .headers(httpHeaders -> request.headers().forEach(httpHeaders::add))
                .body(request.body())
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handle)
                .body(response);
    }

    private URI createUri(String url, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        params.forEach(builder::queryParam);
        return builder.build().toUri();
    }

    @Recover
    public <T> T recover(RestApiClientException e, String path, Map<String, String> queryParams, Class<T> responseType) {
        log.error("최대 재시도 초과 | 요청 경로: {} | 상태 코드: {} | 메시지: {}", path, e.getStatusCode(), e.getMessage());
        //TODO: 문제가 생긴 요청은 무시하고 경매 데이터 저장
        return null;
    }
}
