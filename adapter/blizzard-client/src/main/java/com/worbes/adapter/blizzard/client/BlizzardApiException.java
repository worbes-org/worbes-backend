package com.worbes.adapter.blizzard.client;


import lombok.Getter;

@Getter
public class BlizzardApiException extends RuntimeException {

    private final Integer statusCode;

    public BlizzardApiException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public BlizzardApiException(String message, Throwable cause) {
        super(message, cause);
        statusCode = -1;
    }

    public BlizzardApiException(String message, Integer statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
