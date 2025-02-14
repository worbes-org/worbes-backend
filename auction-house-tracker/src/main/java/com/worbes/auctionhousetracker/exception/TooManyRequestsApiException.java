package com.worbes.auctionhousetracker.exception;

import org.springframework.http.HttpStatus;

public class TooManyRequestsApiException extends BlizzardApiException {
    public TooManyRequestsApiException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS.value());
    }
}
