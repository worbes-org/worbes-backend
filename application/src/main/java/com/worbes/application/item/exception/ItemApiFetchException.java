package com.worbes.application.item.exception;

import lombok.Getter;

@Getter
public class ItemApiFetchException extends RuntimeException {

    private final Integer statusCode;
    private final Long itemId;

    public ItemApiFetchException(String message, Throwable cause, Integer statusCode, Long itemId) {
        super(message, cause);
        this.statusCode = statusCode;
        this.itemId = itemId;
    }

    public ItemApiFetchException(String message, Throwable cause, Long itemId) {
        super(message, cause);
        this.statusCode = -1;
        this.itemId = itemId;
    }
}
