package com.worbes.infra.rest.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RestApiClientException {
    private static final String DEFAULT_MESSAGE = "인증이 필요합니다. 토큰을 확인하세요.";
    private static final int STATUS_CODE = HttpStatus.UNAUTHORIZED.value();

    public UnauthorizedException() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public UnauthorizedException(Throwable cause) {
        super(DEFAULT_MESSAGE, STATUS_CODE, cause);
    }
}

