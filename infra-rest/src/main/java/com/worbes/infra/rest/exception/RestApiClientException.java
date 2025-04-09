package com.worbes.infra.rest.exception;


import lombok.Getter;

@Getter
public class RestApiClientException extends RuntimeException {

    private final Integer statusCode;

    public RestApiClientException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public RestApiClientException(String message, Integer statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
