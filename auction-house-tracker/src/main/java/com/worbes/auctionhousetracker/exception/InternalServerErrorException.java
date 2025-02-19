package com.worbes.auctionhousetracker.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends RestApiClientException {
    public InternalServerErrorException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
