package com.worbes.auctionhousetracker.infrastructure.rest;

import com.worbes.auctionhousetracker.exception.InternalServerErrorException;
import com.worbes.auctionhousetracker.exception.RestApiClientException;
import com.worbes.auctionhousetracker.exception.TooManyRequestsException;
import com.worbes.auctionhousetracker.exception.UnauthorizedException;
import com.worbes.auctionhousetracker.infrastructure.oauth.AccessTokenHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
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


@Slf4j
@Component
public class RestApiClientImpl implements RestApiClient {

    private final RestClient restClient;
    private final AccessTokenHandler accessTokenHandler;

    public RestApiClientImpl(RestClient.Builder builder, AccessTokenHandler accessTokenHandler) {
        this.restClient = builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.accessTokenHandler = accessTokenHandler;
    }

    @Override
    @Retryable(
            recover = "recover",
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 5000, random = true)
    )
    public <T> T get(String url, Map<String, String> params, Class<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        params.forEach(builder::queryParam);
        URI uri = builder.build().toUri();
        return restClient.get()
                .uri(uri)
                .header("Authorization", String.format("Bearer %s", accessTokenHandler.get()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleApiError)
                .body(responseType);
    }

    // API 요청 에러 처리 로직을 별도 메소드로 분리
    private void handleApiError(HttpRequest req, ClientHttpResponse res) {
        try {
            HttpStatusCode statusCode = res.getStatusCode();
            String errorMessage = res.getStatusText();
            String responseBody = new String(res.getBody().readAllBytes()); // 응답 바디 읽기

            String requestUrl = req.getURI().toString(); // 요청 URL 가져오기
            log.error("🔥 API 요청 실패 | URL: {} | 상태 코드: {} | 응답 바디: {}", requestUrl, statusCode.value(), responseBody);

            if (statusCode == HttpStatus.UNAUTHORIZED) {
                log.warn("인증 오류 발생, 토큰 갱신 시작.");
                accessTokenHandler.refresh();
                throw new UnauthorizedException(errorMessage);
            } else if (statusCode == HttpStatus.TOO_MANY_REQUESTS) {
                log.warn("요청 횟수 초과, 재시도 필요.");
                throw new TooManyRequestsException(errorMessage);
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
                log.warn("서버 내부 오류 발생, 잠시 후 다시 시도.");
                throw new InternalServerErrorException(errorMessage);
            }

            log.warn("예상치 못한 API 오류 발생");
            throw new RestApiClientException(errorMessage, statusCode.value());
        } catch (IOException e) {
            log.error("🔥 API 응답 처리 중 예외 발생: {}", e.getMessage());
            throw new RestApiClientException(e.getMessage(), e.getCause());
        }
    }

    // 예외별로 복구 메소드
    @Recover
    public <T> T recover(RestApiClientException e, String path, Map<String, String> queryParams, Class<T> responseType) {
        log.error("🔥 예상치 못한 오류 발생 (최대 재시도 초과) | 요청 경로: {} | 메시지: {}", path, e.getMessage());
        return null;
    }

    @Recover
    public <T> T recover(TooManyRequestsException e, String path, Map<String, String> queryParams, Class<T> responseType) {
        log.error("🔥 API 요청 실패 (최대 재시도 초과) | 요청 경로: {} | 상태 코드: {} | 메시지: {}",
                path, e.getStatusCode(), e.getMessage());
        throw e;
    }

    @Recover
    public <T> T recover(UnauthorizedException e, String path, Map<String, String> queryParams, Class<T> responseType) {
        log.error("🔥 인증 오류 (최대 재시도 초과) | 요청 경로: {} | 상태 코드: {} | 메시지: {}",
                path, e.getStatusCode(), e.getMessage());
        throw e;
    }

    @Recover
    public <T> T recover(InternalServerErrorException e, String path, Map<String, String> queryParams, Class<T> responseType) {
        log.error("🔥 서버 내부 오류 (최대 재시도 초과) | 요청 경로: {} | 상태 코드: {} | 메시지: {}",
                path, e.getStatusCode(), e.getMessage());
        throw e;
    }
}
