package com.worbes.auctionhousetracker.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RestApiClientException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED.value());
    }
}

