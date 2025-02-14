package com.worbes.auctionhousetracker.oauth2;

import com.worbes.auctionhousetracker.exception.BlizzardApiException;
import com.worbes.auctionhousetracker.exception.InternalServerErrorApiException;
import com.worbes.auctionhousetracker.exception.TooManyRequestsApiException;
import com.worbes.auctionhousetracker.exception.UnauthorizedApiException;
import com.worbes.auctionhousetracker.service.AccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;


@Slf4j
@Component
public class ApiCrawlerImpl implements ApiCrawler {

    private static final int MAX_ATTEMPTS = 2;
    private final RestClient restClient;
    private final AccessTokenService tokenService;

    public ApiCrawlerImpl(@Qualifier("apiClient") RestClient restClient, AccessTokenService tokenService) {
        this.restClient = restClient;
        this.tokenService = tokenService;
    }

    @Override
    @Retryable(recover = "recoverFetchData", maxAttempts = MAX_ATTEMPTS, backoff = @Backoff(delay = 1500))
    public <T> T fetchData(String path, Class<T> responseType) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("namespace", "static-kr")
                        .queryParam(":region", "kr")
                        .queryParam("locale", "kr")
                        .build())
                .header("Authorization", String.format("Bearer %s", tokenService.get()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleApiError)
                .body(responseType);
    }

    // API 요청 에러 처리 로직을 별도 메소드로 분리
    private void handleApiError(HttpRequest req, ClientHttpResponse res) {
        try {
            HttpStatusCode statusCode = res.getStatusCode();
            String errorMessage = res.getStatusText();

            if (statusCode == HttpStatus.UNAUTHORIZED) {
                log.warn("인증 오류, 토큰 갱신 시작.");
                tokenService.refresh();
                throw new UnauthorizedApiException(errorMessage);
            } else if (statusCode == HttpStatus.TOO_MANY_REQUESTS) {
                log.warn("요청 횟수 초과, 잠시 후 다시 시도.");
                throw new TooManyRequestsApiException(errorMessage);
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
                log.warn("블리자드 서버 내부 오류 발생, 잠시 후 다시 시도.");
                throw new InternalServerErrorApiException(errorMessage);
            }
            log.warn("예상치 못한 오류 발생");
            throw new BlizzardApiException(errorMessage, statusCode.value());
        } catch (IOException e) {
            throw new BlizzardApiException(e.getMessage());
        }
    }

    // 예외별로 복구 메소드
    @Recover
    public  <T> T recoverFetchData(BlizzardApiException e, String path, Class<T> responseType) {
        log.error("🔥 예상치 못한 오류 발생 (최대 재시도 초과) | 요청 경로: {} | 메시지: {}", path, e.getMessage());
        throw e;
    }

    @Recover
    public <T> T recoverFetchData(TooManyRequestsApiException e, String path, Class<T> responseType) {
        log.error("🔥 API 요청 실패 (최대 재시도 초과) | 요청 경로: {} | 상태 코드: {} | 메시지: {}",
                path, e.getStatusCode(), e.getMessage());
        throw e;
    }

    @Recover
    public <T> T recoverFetchData(UnauthorizedApiException e, String path, Class<T> responseType) {
        log.error("🔥 인증 오류 (최대 재시도 초과) | 요청 경로: {} | 상태 코드: {} | 메시지: {}",
                path, e.getStatusCode(), e.getMessage());
        throw e;
    }

    @Recover
    public <T> T recoverFetchData(InternalServerErrorApiException e, String path, Class<T> responseType) {
        log.error("🔥 블리자드 서버 내부 오류 (최대 재시도 초과) | 요청 경로: {} | 상태 코드: {} | 메시지: {}",
                path, e.getStatusCode(), e.getMessage());
        throw e;
    }
}
