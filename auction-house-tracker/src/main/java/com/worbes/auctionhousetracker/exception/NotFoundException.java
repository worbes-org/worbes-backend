package com.worbes.auctionhousetracker.exception;

public class NotFoundException extends RestApiClientException {
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
