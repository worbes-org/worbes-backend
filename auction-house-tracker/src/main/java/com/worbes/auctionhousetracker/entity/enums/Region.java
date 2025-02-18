package com.worbes.auctionhousetracker.entity.enums;

public enum Region {
    US("us"),
    KR("kr");

    private final String value;

    Region(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
