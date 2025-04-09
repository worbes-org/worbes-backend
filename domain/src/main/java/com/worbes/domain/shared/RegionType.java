package com.worbes.domain.shared;

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
