package com.worbes.auctionhousetracker.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BlizzardApiException extends RuntimeException {

    private final int statusCode; // HTTP 상태 코드
    private final String errorBody; // 응답 본문(선택적)

    public BlizzardApiException(String message) {
        super(message);
        this.statusCode = 0; // 상태 코드가 없는 경우 기본값
        this.errorBody = null;
    }

    public BlizzardApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.errorBody = null;
    }

    public BlizzardApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorBody = null;
    }

    public BlizzardApiException(String message, int statusCode, String errorBody) {
        super(message);
        this.statusCode = statusCode;
        this.errorBody = errorBody;
    }
}
