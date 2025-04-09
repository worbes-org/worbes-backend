package com.worbes.auctionhousetracker.entity.enums;

import lombok.Getter;

@Getter
public enum RegionType {
    US("us"),
    KR("kr");

    private final String value;

    RegionType(String value) {
        this.value = value;
    }

}
