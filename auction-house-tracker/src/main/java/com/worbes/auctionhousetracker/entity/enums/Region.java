package com.worbes.auctionhousetracker.entity.enums;

import lombok.Getter;

@Getter
public enum Region {
    US("us"),
    KR("kr");

    private final String value;

    Region(String value) {
        this.value = value;
    }

}
