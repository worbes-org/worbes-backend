package com.worbes.infra.rest.client;

import com.worbes.infra.rest.exception.InternalServerErrorException;
import com.worbes.infra.rest.exception.RestApiClientException;
import com.worbes.infra.rest.exception.TooManyRequestsException;
import com.worbes.infra.rest.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestApiErrorHandlerImpl implements RestApiErrorHandler {

    private final AccessTokenHandler accessTokenHandler;

    @Override
    public void handle(HttpRequest req, ClientHttpResponse res) {
        try {
            HttpStatusCode statusCode = res.getStatusCode();
            String statusText = res.getStatusText();
            String requestUrl = req.getURI().toString();
            log.error("API 요청 실패 | URL: {} | 상태 코드: {} | 상태 메세지: {}", requestUrl, statusCode.value(), statusText);

            if (statusCode.equals(UNAUTHORIZED)) {
                log.warn("⚠️ 401 Unauthorized 발생 - 토큰 갱신 시도");
                accessTokenHandler.refresh();
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
}
