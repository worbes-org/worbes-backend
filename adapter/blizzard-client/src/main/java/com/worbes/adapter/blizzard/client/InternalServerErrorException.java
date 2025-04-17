package com.worbes.adapter.blizzard.client;

public class InternalServerErrorException extends BlizzardApiException {
    private static final String DEFAULT_MESSAGE = "서버 내부 오류가 발생했습니다.";
    private static final int STATUS_CODE = 500;

    public InternalServerErrorException(String message) {
        super(message, STATUS_CODE);
    }

    public InternalServerErrorException() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public InternalServerErrorException(Throwable cause) {
        super(DEFAULT_MESSAGE, STATUS_CODE, cause);
    }
}
