package com.worbes.auctionhousetracker.exception;


import lombok.Getter;

@Getter
public class RestApiClientException extends RuntimeException {
    private final int statusCode;

    public RestApiClientException(String message) {
        super(message);
        this.statusCode = 0;
    }

    public RestApiClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
