package com.worbes.infra.rest.core.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RestApiClientException {
    private static final String DEFAULT_MESSAGE = "인증이 필요합니다.";
    private static final int STATUS_CODE = HttpStatus.UNAUTHORIZED.value();

    public UnauthorizedException(String message) {
        super(message, STATUS_CODE);
    }

    public UnauthorizedException() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public UnauthorizedException(Throwable cause) {
        super(DEFAULT_MESSAGE, STATUS_CODE, cause);
    }
}

