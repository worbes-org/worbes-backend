package com.worbes.infra.rest.core.client;

import com.worbes.infra.rest.core.exception.InternalServerErrorException;
import com.worbes.infra.rest.core.exception.RestApiClientException;
import com.worbes.infra.rest.core.exception.TooManyRequestsException;
import com.worbes.infra.rest.core.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DefaultRestApiErrorHandlerTest {

    @Mock
    private HttpRequest request;

    @Mock
    private ClientHttpResponse response;

    @InjectMocks
    private DefaultRestApiErrorHandler handler;

    @Test
    @DisplayName("401 Unauthorized 응답이면 UnauthorizedException 발생")
    void shouldThrowUnauthorizedExceptionOn401() throws IOException {
        given(request.getMethod()).willReturn(HttpMethod.GET);
        given(request.getURI()).willReturn(URI.create("https://api.example.com/test"));
        given(response.getStatusCode()).willReturn(HttpStatus.UNAUTHORIZED);
        given(response.getBody()).willReturn(new ByteArrayInputStream("인증 오류".getBytes()));

        assertThatThrownBy(() -> handler.handle(request, response))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("인증 오류");
    }

    @Test
    @DisplayName("429 Too Many Requests 응답이면 TooManyRequestsException 발생")
    void shouldThrowTooManyRequestsExceptionOn429() throws IOException {
        given(request.getMethod()).willReturn(HttpMethod.GET);
        given(request.getURI()).willReturn(URI.create("https://api.example.com/test"));
        given(response.getStatusCode()).willReturn(HttpStatus.TOO_MANY_REQUESTS);
        given(response.getBody()).willReturn(new ByteArrayInputStream("요청 제한".getBytes()));

        assertThatThrownBy(() -> handler.handle(request, response))
                .isInstanceOf(TooManyRequestsException.class)
                .hasMessage("요청 제한");
    }

    @Test
    @DisplayName("500 Internal Server Error 응답이면 InternalServerErrorException 발생")
    void shouldThrowInternalServerErrorExceptionOn500() throws IOException {
        given(request.getMethod()).willReturn(HttpMethod.GET);
        given(request.getURI()).willReturn(URI.create("https://api.example.com/test"));
        given(response.getStatusCode()).willReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        given(response.getBody()).willReturn(new ByteArrayInputStream("서버 오류".getBytes()));

        assertThatThrownBy(() -> handler.handle(request, response))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("서버 오류");
    }

    @Test
    @DisplayName("기타 상태 코드 응답이면 RestApiClientException 발생")
    void shouldThrowRestApiClientExceptionForOtherStatus() throws IOException {
        given(request.getMethod()).willReturn(HttpMethod.GET);
        given(request.getURI()).willReturn(URI.create("https://api.example.com/test"));
        given(response.getStatusCode()).willReturn(HttpStatus.BAD_REQUEST);
        given(response.getBody()).willReturn(new ByteArrayInputStream("잘못된 요청".getBytes()));

        assertThatThrownBy(() -> handler.handle(request, response))
                .isInstanceOf(RestApiClientException.class)
                .hasMessage("잘못된 요청")
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("IOException 발생 시 RestApiClientException으로 래핑됨")
    void shouldWrapIOExceptionAsRestApiClientException() throws IOException {
        given(response.getStatusCode()).willThrow(new IOException("읽기 실패"));

        assertThatThrownBy(() -> handler.handle(request, response))
                .isInstanceOf(RestApiClientException.class)
                .hasMessage("읽기 실패");
    }
}
