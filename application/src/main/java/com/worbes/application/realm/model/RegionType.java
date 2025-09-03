package com.worbes.application.realm.model;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum RegionType implements Serializable {

    US("us"),
    KR("kr");

    private final String value;

    RegionType(String value) {
        this.value = value;
    }

    public static RegionType fromValue(String value) {
        for (RegionType region : values()) {
            if (region.value.equalsIgnoreCase(value)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Invalid region: " + value);
    }
}
