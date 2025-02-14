package com.worbes.auctionhousetracker.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedApiException extends BlizzardApiException {
    public UnauthorizedApiException(String message) {
        super(message, HttpStatus.UNAUTHORIZED.value());
    }
}

