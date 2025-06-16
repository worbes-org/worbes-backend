package com.worbes.adapter.blizzard.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class BlizzardApiErrorHandler implements RestClientErrorHandler {

    @Override
    public void handle(HttpRequest request, ClientHttpResponse response) {
        try {
            HttpStatusCode statusCode = response.getStatusCode();
            String responseBody = new String(response.getBody().readAllBytes());
            String requestUrl = request.getURI().toString();

            log.error("Blizzard API 요청 실패 | 메서드: {} | URL: {} | 상태 코드: {} | 메시지: {}",
                    request.getMethod(), requestUrl, statusCode.value(), responseBody);

            throw switch (statusCode.value()) {
                case 401 -> new UnauthorizedException(responseBody);
                case 429 -> new TooManyRequestsException(responseBody);
                case 500, 502, 503 -> new InternalServerErrorException(responseBody);
                default -> new BlizzardApiException(responseBody, statusCode.value());
            };
        } catch (IOException e) {
            log.error("Blizzard API 응답 처리 중 IOException 발생: {}", e.getMessage());
            throw new BlizzardApiException(e.getMessage(), e);
        }
    }
}
