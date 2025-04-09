package com.worbes.infra.rest.client;

import com.worbes.infra.rest.exception.InternalServerErrorException;
import com.worbes.infra.rest.exception.RestApiClientException;
import com.worbes.infra.rest.exception.TooManyRequestsException;
import com.worbes.infra.rest.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
public class RestApiClientImpl implements RestApiClient {

    private final RestClient restClient;
    private final AccessTokenClient accessTokenClient;

    public RestApiClientImpl(RestClient.Builder builder, AccessTokenClient accessTokenClient) {
        this.restClient = builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.accessTokenClient = accessTokenClient;
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
                .onStatus(HttpStatusCode::isError, this::handleApiError)
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
                .onStatus(HttpStatusCode::isError, this::handleApiError)
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
        return Map.of("Authorization", String.format("Bearer %s", accessTokenClient.get()));
    }

    // API 요청 에러 처리 로직을 별도 메소드로 분리
    private void handleApiError(HttpRequest req, ClientHttpResponse res) {
        try {
            HttpStatusCode statusCode = res.getStatusCode();
            String statusText = res.getStatusText();
            String requestUrl = req.getURI().toString();
            log.error("API 요청 실패 | URL: {} | 상태 코드: {} | 상태 메세지: {}", requestUrl, statusCode.value(), statusText);

            if (statusCode.equals(UNAUTHORIZED)) {
                log.warn("⚠️ 401 Unauthorized 발생 - 토큰 갱신 시도");
                accessTokenClient.refresh();
                throw new UnauthorizedException();
            }
            if (statusCode.equals(NOT_FOUND)) {
                log.error("🚨 404 Not Found - 해당 데이터 없음");
                throw new NoSuchElementException();
            }
            if (statusCode.equals(INTERNAL_SERVER_ERROR)) {
                log.error("🔥 500 Internal Server Error - 서버 문제 발생");
                throw new InternalServerErrorException();
            }
            if (statusCode.equals(TOO_MANY_REQUESTS)) {
                log.warn("⏳ 429 Too Many Requests - 요청 제한 초과");
                throw new TooManyRequestsException();
            }

            throw new RestApiClientException("API 오류 발생", statusCode.value());
        } catch (IOException e) {
            log.error("API 응답 처리 중 IOException 발생: {}", e.getMessage());
            throw new RestApiClientException(e.getMessage(), 0, e);
        }
    }

    @Recover
    public <T> T recover(RestApiClientException e, String path, Map<String, String> queryParams, Class<T> responseType) {
        log.error("최대 재시도 초과 | 요청 경로: {} | 상태 코드: {} | 메시지: {}", path, e.getStatusCode(), e.getMessage());
        //TODO: 문제가 생긴 요청은 무시하고 경매 데이터 저장
        return null;
    }
}
