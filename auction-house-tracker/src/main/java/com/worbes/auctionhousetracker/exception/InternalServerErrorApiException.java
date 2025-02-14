package com.worbes.auctionhousetracker.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorApiException extends BlizzardApiException {
    public InternalServerErrorApiException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
