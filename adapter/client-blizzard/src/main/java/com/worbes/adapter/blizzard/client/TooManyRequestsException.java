package com.worbes.adapter.blizzard.client;

public class TooManyRequestsException extends BlizzardApiException {
    private static final String DEFAULT_MESSAGE = "요청이 너무 많습니다. 나중에 다시 시도하세요.";
    private static final int STATUS_CODE = 429;

    public TooManyRequestsException(String message) {
        super(message, STATUS_CODE);
    }

    public TooManyRequestsException() {
        super(DEFAULT_MESSAGE, STATUS_CODE);
    }

    public TooManyRequestsException(Throwable cause) {
        super(DEFAULT_MESSAGE, STATUS_CODE, cause);
    }
}
