package com.worbes.auctionhousetracker.exception;


import lombok.Getter;

@Getter
public class BlizzardApiException extends RuntimeException {
    private final int statusCode;

    public BlizzardApiException(String message) {
        super(message);
        this.statusCode = 0;
    }

    public BlizzardApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
