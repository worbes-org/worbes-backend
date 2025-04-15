package com.worbes.infra.rest.core.client;

import com.worbes.infra.rest.core.exception.InternalServerErrorException;
import com.worbes.infra.rest.core.exception.RestApiClientException;
import com.worbes.infra.rest.core.exception.TooManyRequestsException;
import com.worbes.infra.rest.core.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class DefaultRestApiErrorHandler implements RestApiErrorHandler {

    @Override
    public void handle(HttpRequest request, ClientHttpResponse response) {
        try {
            HttpStatusCode statusCode = response.getStatusCode();
            String responseBody = new String(response.getBody().readAllBytes());
            String requestUrl = request.getURI().toString();

            log.error("API 요청 실패 | 메서드: {} | URL: {} | 상태 코드: {} | 메시지: {}",
                    request.getMethod(), requestUrl, statusCode.value(), responseBody);

            switch (statusCode.value()) {
                case 401 -> throw new UnauthorizedException(responseBody);
                case 429 -> throw new TooManyRequestsException(responseBody);
                case 500 -> throw new InternalServerErrorException(responseBody);
                default -> throw new RestApiClientException(responseBody, statusCode.value());
            }
        } catch (IOException e) {
            log.error("API 응답 처리 중 IOException 발생: {}", e.getMessage());
            throw new RestApiClientException(e.getMessage(), e);
        }
    }
}
