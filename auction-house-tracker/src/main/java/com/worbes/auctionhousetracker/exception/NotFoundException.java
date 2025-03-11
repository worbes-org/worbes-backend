package com.worbes.auctionhousetracker.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RestApiClientException {
    private static final String DEFAULT_MESSAGE = "요청한 리소스를 찾을 수 없습니다.";
    private static final int STATUS_CODE = HttpStatus.NOT_FOUND.value();

    public NotFoundException() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public NotFoundException(Throwable cause) {
        super(DEFAULT_MESSAGE, STATUS_CODE, cause);
    }
}
