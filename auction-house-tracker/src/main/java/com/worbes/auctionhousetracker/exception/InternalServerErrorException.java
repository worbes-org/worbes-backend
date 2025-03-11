package com.worbes.auctionhousetracker.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends RestApiClientException {
    private static final String DEFAULT_MESSAGE = "서버 내부 오류가 발생했습니다.";
    private static final int STATUS_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();

    public InternalServerErrorException() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public InternalServerErrorException(Throwable cause) {
        super(DEFAULT_MESSAGE, STATUS_CODE, cause);
    }
}
