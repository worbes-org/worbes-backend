package com.worbes.adapter.blizzard.client;

public class UnauthorizedException extends BlizzardApiException {
    private static final String DEFAULT_MESSAGE = "인증이 필요합니다.";
    private static final int STATUS_CODE = 401;

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

