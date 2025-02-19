package com.worbes.auctionhousetracker.exception;

import org.springframework.http.HttpStatus;

public class TooManyRequestsException extends RestApiClientException {
    public TooManyRequestsException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS.value());
    }
}
