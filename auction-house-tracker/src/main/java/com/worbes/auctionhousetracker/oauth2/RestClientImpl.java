package com.worbes.auctionhousetracker.oauth2;

import com.worbes.auctionhousetracker.exception.BlizzardApiException;
import com.worbes.auctionhousetracker.exception.InternalServerErrorApiException;
import com.worbes.auctionhousetracker.exception.TooManyRequestsApiException;
import com.worbes.auctionhousetracker.exception.UnauthorizedApiException;
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
import java.util.Map;


@Slf4j
@Component
public class RestClientImpl implements RestApiClient {

    private static final int MAX_ATTEMPTS = 2;
    private static final int BACK_OFF_DELAY = 1500;
    private final RestClient restClient;
    private final AccessTokenService tokenService;

    public RestClientImpl(@Qualifier("apiClient") RestClient restClient, AccessTokenService tokenService) {
        this.restClient = restClient;
        this.tokenService = tokenService;
    }

    @Override
    @Retryable(recover = "recover", maxAttempts = MAX_ATTEMPTS, backoff = @Backoff(delay = BACK_OFF_DELAY))
    public <T> T get(String path, Map<String, String> params, Class<T> responseType) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(path);
                    params.forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                })
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
            String responseBody = new String(res.getBody().readAllBytes()); // 응답 바디 읽기

            String requestUrl = req.getURI().toString(); // 요청 URL 가져오기
            log.error("🔥 API 요청 실패 | URL: {} | 상태 코드: {} | 응답 바디: {}", requestUrl, statusCode.value(), responseBody);

            if (statusCode == HttpStatus.UNAUTHORIZED) {
                log.warn("인증 오류 발생, 토큰 갱신 시작.");
                tokenService.refresh();
                throw new UnauthorizedApiException(errorMessage);
            } else if (statusCode == HttpStatus.TOO_MANY_REQUESTS) {
                log.warn("요청 횟수 초과, 재시도 필요.");
                throw new TooManyRequestsApiException(errorMessage);
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
                log.warn("블리자드 서버 내부 오류 발생, 잠시 후 다시 시도.");
                throw new InternalServerErrorApiException(errorMessage);
            }

            log.warn("예상치 못한 API 오류 발생");
            throw new BlizzardApiException(errorMessage, statusCode.value());
        } catch (IOException e) {
            log.error("🔥 API 응답 처리 중 예외 발생: {}", e.getMessage());
            throw new BlizzardApiException(e.getMessage());
        }
    }

    // 예외별로 복구 메소드
    @Recover
    public <T> T recover(BlizzardApiException e, String path, Map<String, String> queryParams, Class<T> responseType) {
        log.error("🔥 예상치 못한 오류 발생 (최대 재시도 초과) | 요청 경로: {} | 메시지: {}", path, e.getMessage());
        throw e;
    }

    @Recover
    public <T> T recover(TooManyRequestsApiException e, String path, Map<String, String> queryParams, Class<T> responseType) {
        log.error("🔥 API 요청 실패 (최대 재시도 초과) | 요청 경로: {} | 상태 코드: {} | 메시지: {}",
                path, e.getStatusCode(), e.getMessage());
        throw e;
    }

    @Recover
    public <T> T recover(UnauthorizedApiException e, String path, Map<String, String> queryParams, Class<T> responseType) {
        log.error("🔥 인증 오류 (최대 재시도 초과) | 요청 경로: {} | 상태 코드: {} | 메시지: {}",
                path, e.getStatusCode(), e.getMessage());
        throw e;
    }

    @Recover
    public <T> T recover(InternalServerErrorApiException e, String path, Map<String, String> queryParams, Class<T> responseType) {
        log.error("🔥 블리자드 서버 내부 오류 (최대 재시도 초과) | 요청 경로: {} | 상태 코드: {} | 메시지: {}",
                path, e.getStatusCode(), e.getMessage());
        throw e;
    }
}
