package com.worbes.infra.rest.core.exception;


import lombok.Getter;

@Getter
public class RestApiClientException extends RuntimeException {

    private final Integer statusCode;

    public RestApiClientException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public RestApiClientException(String message, Throwable cause) {
        super(message, cause);
        statusCode = -1;
    }

    public RestApiClientException(String message, Integer statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
