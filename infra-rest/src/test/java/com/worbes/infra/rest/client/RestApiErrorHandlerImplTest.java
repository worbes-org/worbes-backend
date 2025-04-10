package com.worbes.infra.rest.client;

import com.worbes.infra.rest.exception.InternalServerErrorException;
import com.worbes.infra.rest.exception.RestApiClientException;
import com.worbes.infra.rest.exception.TooManyRequestsException;
import com.worbes.infra.rest.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class RestApiErrorHandlerImplTest {

    RestApiErrorHandlerImpl handler = new RestApiErrorHandlerImpl();

    HttpRequest mockRequest(String uri) {
        HttpRequest request = mock(HttpRequest.class);
        given(request.getURI()).willReturn(URI.create(uri));
        return request;
    }

    ClientHttpResponse mockResponse(HttpStatus status) throws IOException {
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        given(response.getStatusCode()).willReturn(status);
        given(response.getStatusText()).willReturn(status.getReasonPhrase());
        return response;
    }

    @Nested
    @DisplayName("handle()은 HTTP 상태 코드에 따라 예외를 던진다")
    class ExceptionMappingTest {

        @Test
        void shouldThrowUnauthorizedException_when401() throws IOException {
            HttpRequest request = mockRequest("https://api.worbes.com");
            ClientHttpResponse response = mockResponse(HttpStatus.UNAUTHORIZED);

            assertThatThrownBy(() -> handler.handle(request, response))
                    .isInstanceOf(UnauthorizedException.class);
        }

        @Test
        void shouldThrowNotFoundException_when404() throws IOException {
            HttpRequest request = mockRequest("https://api.worbes.com");
            ClientHttpResponse response = mockResponse(HttpStatus.NOT_FOUND);

            assertThatThrownBy(() -> handler.handle(request, response))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void shouldThrowInternalServerErrorException_when500() throws IOException {
            HttpRequest request = mockRequest("https://api.worbes.com");
            ClientHttpResponse response = mockResponse(HttpStatus.INTERNAL_SERVER_ERROR);

            assertThatThrownBy(() -> handler.handle(request, response))
                    .isInstanceOf(InternalServerErrorException.class);
        }

        @Test
        void shouldThrowTooManyRequestsException_when429() throws IOException {
            HttpRequest request = mockRequest("https://api.worbes.com");
            ClientHttpResponse response = mockResponse(HttpStatus.TOO_MANY_REQUESTS);

            assertThatThrownBy(() -> handler.handle(request, response))
                    .isInstanceOf(TooManyRequestsException.class);
        }

        @Test
        void shouldThrowGenericRestApiClientException_whenUnhandledStatus() throws IOException {
            HttpRequest request = mockRequest("https://api.worbes.com");
            ClientHttpResponse response = mockResponse(HttpStatus.I_AM_A_TEAPOT);

            assertThatThrownBy(() -> handler.handle(request, response))
                    .isInstanceOf(RestApiClientException.class);
        }

        @Test
        void shouldThrowRestApiClientException_whenIOExceptionOccurs() throws IOException {
            HttpRequest request = mockRequest("https://api.worbes.com");
            ClientHttpResponse response = mock(ClientHttpResponse.class);

            given(response.getStatusCode()).willThrow(new IOException("Boom"));

            assertThatThrownBy(() -> handler.handle(request, response))
                    .isInstanceOf(RestApiClientException.class)
                    .hasMessageContaining("Boom");
        }
    }
}
